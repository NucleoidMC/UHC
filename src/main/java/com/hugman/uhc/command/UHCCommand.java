package com.hugman.uhc.command;

import com.hugman.uhc.command.argument.UHCModuleArgument;
import com.hugman.uhc.config.UHCGameConfig;
import com.hugman.uhc.game.UHCAttachments;
import com.hugman.uhc.module.Module;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import java.util.Objects;

public class UHCCommand {
    private static final SimpleCommandExceptionType NO_MANAGER_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.no_manager"));
    private static final SimpleCommandExceptionType NO_MODULES_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.no_modules_activated"));
    private static final SimpleCommandExceptionType COULD_NOT_ACTIVATE_MODULE = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.enable.error"));

    private static final String MODULE_ARG = "module";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
                CommandManager.literal("uhc")
                        .then(CommandManager.literal("create")
                                .requires(UHCCommand::isSourceNotInUHC)
                                .executes(context -> 0))
                        .then(CommandManager.literal("modules")
                                .requires(UHCCommand::isSourceInUHC) //TODO: check if there is a module manager
                                .executes(UHCCommand::displayModules)
                                .then(CommandManager.literal("enable")
                                        .then(UHCModuleArgument.argument("module")
                                        .executes(context -> enableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
                                .then(CommandManager.literal("disable")
                                        .then(UHCModuleArgument.argument("module") //TODO: only suggest enabled modules
                                        .executes(context -> disableModule(context, UHCModuleArgument.get(context, MODULE_ARG))))))

        );
    }

    public static boolean isSourceInUHC(ServerCommandSource source) {
        GameSpace gameSpace = GameSpaceManager.get().byWorld(source.getWorld());
        if (gameSpace != null) {
            return gameSpace.getMetadata().sourceConfig().value().config() instanceof UHCGameConfig;
        }
        return false;
    }

    public static boolean isSourceNotInUHC(ServerCommandSource source) {
        return !isSourceInUHC(source);
    }

    private static int displayModules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld())).getAttachment(UHCAttachments.MODULE_MANAGER);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (!manager.isEmpty()) {
            manager.buildGui(source.getPlayer()).open();
            return Command.SINGLE_SUCCESS;
        } else {
            throw NO_MODULES_ACTIVATED.create();
        }
    }

    private static int enableModule(CommandContext<ServerCommandSource> context, RegistryEntry<Module> module) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld())).getAttachment(UHCAttachments.MODULE_MANAGER);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.enableModule(module)) {
            source.sendFeedback(() -> Text.translatable("command.uhc.modules.enable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw COULD_NOT_ACTIVATE_MODULE.create();
        }
    }

    private static int disableModule(CommandContext<ServerCommandSource> context, RegistryEntry<Module> module) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld())).getAttachment(UHCAttachments.MODULE_MANAGER);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.disableModule(module)) {
            source.sendFeedback(() -> Text.translatable("command.uhc.modules.enable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw COULD_NOT_ACTIVATE_MODULE.create();
        }
    }
}
