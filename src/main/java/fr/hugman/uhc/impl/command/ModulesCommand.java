package fr.hugman.uhc.impl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import fr.hugman.uhc.api.command.argument.UHCModuleArgument;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModuleEvents;
import fr.hugman.uhc.impl.game.ModuleManager;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;

import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class ModulesCommand {
    private static final SimpleCommandExceptionType NO_MANAGER_ACTIVATED = new SimpleCommandExceptionType(Component.translatable("command.modules.no_manager"));
    private static final SimpleCommandExceptionType NO_MODULES_ACTIVATED = new SimpleCommandExceptionType(Component.translatable("command.modules.no_modules_activated"));
    private static final SimpleCommandExceptionType ALREADY_ENABLED = new SimpleCommandExceptionType(Component.translatable("command.modules.already_enabled"));
    private static final SimpleCommandExceptionType ALREADY_DISABLED = new SimpleCommandExceptionType(Component.translatable("command.modules.already_disabled"));

    private static final String MODULE_ARG = "module";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("modules")
                        .requires(ModulesCommand::supportsModules)
                        .executes(ModulesCommand::displayModules)
                        .then(Commands.literal("enable")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(UHCModuleArgument.argumentFromDisabled("module")
                                        .executes(context -> enableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
                        .then(Commands.literal("disable")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(UHCModuleArgument.argumentFromEnabled("module")
                                        .executes(context -> disableModule(context, UHCModuleArgument.get(context, MODULE_ARG)))))
        );
    }

    public static boolean supportsModules(CommandSourceStack source) {
        var level = source.getLevel();
        if(level == null) {
            return false;
        }
        GameSpace gameSpace = GameSpaceManager.get().byLevel(level);
        if (gameSpace == null) {
            return false;
        }
        if (!(gameSpace.getAttachment(ModuleManager.ATTACHMENT) instanceof ModuleManager)) {
            return false;
        }
        return true;
    }

    private static int displayModules(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
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

    private static int enableModule(CommandContext<CommandSourceStack> context, Holder<UHCModule> module) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        var space = Objects.requireNonNull(GameSpaceManager.get().byLevel(source.getLevel()));
        var manager = space.getAttachment(ModuleManager.ATTACHMENT);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.enableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(UHCModuleEvents.ENABLE)).onEnable(module);
            }

            source.sendSuccess(() -> Component.translatable("command.modules.enable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw ALREADY_ENABLED.create();
        }
    }

    private static int disableModule(CommandContext<CommandSourceStack> context, Holder<UHCModule> module) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        var manager = Objects.requireNonNull(GameSpaceManager.get().byLevel(source.getLevel())).getAttachment(ModuleManager.ATTACHMENT);
        if (manager == null) {
            throw NO_MANAGER_ACTIVATED.create();
        }

        if (manager.disableModule(module)) {
            try (EventInvokers invokers = Stimuli.select().forCommandSource(context.getSource())) {
                (invokers.get(UHCModuleEvents.DISABLE)).onDisable(module);
            }

            source.sendSuccess(() -> Component.translatable("command.modules.disable.success", module.value().name()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            throw ALREADY_DISABLED.create();
        }
    }
}
