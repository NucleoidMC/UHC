package com.hugman.uhc.command;

import com.hugman.uhc.command.argument.UHCModuleArgument;
import com.hugman.uhc.config.UHCGameConfig;
import com.hugman.uhc.game.UHCAttachments;
import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.ModuleEvents;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

import java.util.Objects;

public class UHCCommand {
    private static final SimpleCommandExceptionType NO_MANAGER_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.no_manager")); //TODO: translate this
    private static final SimpleCommandExceptionType NO_MODULES_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.no_modules_activated")); //TODO: translate this
    private static final SimpleCommandExceptionType COULD_NOT_ENABLE_MODULE = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.enable.error")); //TODO: translate this
    private static final SimpleCommandExceptionType COULD_NOT_DISABLE_MODULE = new SimpleCommandExceptionType(Text.translatable("command.uhc.modules.disable.error")); //TODO: translate this

    private static final String MODULE_ARG = "module";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("uhc")
                        .then(CommandManager.literal("create")
                                .requires(UHCCommand::isNotUHC)
                                .executes(UHCCommand::createUHC))
                        .then(CommandManager.literal("modules")
                                .requires(UHCCommand::isUHC)
                                .executes(UHCCommand::displayModules)
                                .then(CommandManager.literal("enable")
                                        .then(UHCModuleArgument.argumentFromDisabled("module")
                                                .executes(context -> enableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
                                .then(CommandManager.literal("disable")
                                        .then(UHCModuleArgument.argumentFromEnabled("module")
                                                .executes(context -> disableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
                        )
        );
    }

    public static boolean isUHC(ServerCommandSource source) {
        GameSpace gameSpace = GameSpaceManager.get().byWorld(source.getWorld());
        if (gameSpace == null) {
            return false;
        }
        if (!(gameSpace.getMetadata().sourceConfig().value().config() instanceof UHCGameConfig)) {
            return false;
        }
        return true;
    }

    public static boolean isNotUHC(ServerCommandSource source) {
        return !isUHC(source);
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
        var space = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld()));
        var manager = space.getAttachment(UHCAttachments.MODULE_MANAGER);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.enableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(ModuleEvents.ENABLE)).onEnable(module);
            }

            source.sendFeedback(() -> Text.translatable("command.uhc.modules.enable.success", module.value().name()), true); //TODO: translate this
            return Command.SINGLE_SUCCESS;
        } else {
            throw COULD_NOT_ENABLE_MODULE.create();
        }
    }

    private static int disableModule(CommandContext<ServerCommandSource> context, RegistryEntry<Module> module) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld())).getAttachment(UHCAttachments.MODULE_MANAGER);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.disableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(ModuleEvents.DISABLE)).onDisable(module);
            }

            source.sendFeedback(() -> Text.translatable("command.uhc.modules.disable.success", module.value().name()), true); //TODO: translate this
            return Command.SINGLE_SUCCESS;
        } else {
            throw COULD_NOT_DISABLE_MODULE.create();
        }
    }


    private static int createUHC(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.of("Available soon... :)"), true);
        return Command.SINGLE_SUCCESS;
    }
}
