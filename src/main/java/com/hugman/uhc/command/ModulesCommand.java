package com.hugman.uhc.command;

import com.hugman.uhc.command.argument.UHCModuleArgument;
import com.hugman.uhc.game.ModuleManager;
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

public class ModulesCommand {
    private static final SimpleCommandExceptionType NO_MANAGER_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.modules.no_manager"));
    private static final SimpleCommandExceptionType NO_MODULES_ACTIVATED = new SimpleCommandExceptionType(Text.translatable("command.modules.no_modules_activated"));
    private static final SimpleCommandExceptionType ALREADY_ENABLED = new SimpleCommandExceptionType(Text.translatable("command.modules.already_enabled"));
    private static final SimpleCommandExceptionType ALREADY_DISABLED = new SimpleCommandExceptionType(Text.translatable("command.modules.already_disabled"));

    private static final String MODULE_ARG = "module";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("modules")
                        .requires(ModulesCommand::supportsModules)
                        .executes(ModulesCommand::displayModules)
                        .then(CommandManager.literal("enable")
                                .then(UHCModuleArgument.argumentFromDisabled("module")
                                        .executes(context -> enableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
                        .then(CommandManager.literal("disable")
                                .then(UHCModuleArgument.argumentFromEnabled("module")
                                        .executes(context -> disableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
        );
    }

    public static boolean supportsModules(ServerCommandSource source) {
        GameSpace gameSpace = GameSpaceManager.get().byWorld(source.getWorld());
        if (gameSpace == null) {
            return false;
        }
        if (!(gameSpace.getAttachment(ModuleManager.ATTACHMENT) instanceof ModuleManager)) {
            return false;
        }
        return true;
    }

    private static int displayModules(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byPlayer(source.getPlayer())).getAttachment(ModuleManager.ATTACHMENT);
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
        var manager = space.getAttachment(ModuleManager.ATTACHMENT);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.enableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(ModuleEvents.ENABLE)).onEnable(module);
            }

            source.sendFeedback(() -> Text.translatable("command.modules.enable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw ALREADY_ENABLED.create();
        }
    }

    private static int disableModule(CommandContext<ServerCommandSource> context, RegistryEntry<Module> module) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(source.getWorld())).getAttachment(ModuleManager.ATTACHMENT);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.disableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(ModuleEvents.DISABLE)).onDisable(module);
            }

            source.sendFeedback(() -> Text.translatable("command.modules.disable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw ALREADY_DISABLED.create();
        }
    }
}
