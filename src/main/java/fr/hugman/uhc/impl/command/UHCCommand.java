package fr.hugman.uhc.impl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.hugman.uhc.api.command.argument.UHCConfigArgument;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.gui.creator.CreateUHCGui;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class UHCCommand {
    private static final String CONFIG_ARG = "config";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("uhc")
                .then(CommandManager.literal("create")
                        .then(CommandManager.literal("from")
                                .then(UHCConfigArgument.argument(CONFIG_ARG)
                                        .executes(context -> displayCreator(context, UHCConfigArgument.get(context, CONFIG_ARG))))
                        )
                )
        );
    }

    private static int displayCreator(CommandContext<ServerCommandSource> context, RegistryEntry<UHCConfig> entry) {
        var clone = entry.value().clone();
        new CreateUHCGui(context.getSource().getPlayer(), clone).open();
        return Command.SINGLE_SUCCESS;
    }
}
