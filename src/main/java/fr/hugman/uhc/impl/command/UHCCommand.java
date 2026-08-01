package fr.hugman.uhc.impl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.hugman.uhc.api.command.argument.UHCConfigArgument;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.gui.creator.CreateUHCGui;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;

public class UHCCommand {
    private static final String CONFIG_ARG = "config";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("uhc")
                .then(Commands.literal("create")
                        .then(Commands.literal("from")
                                .then(UHCConfigArgument.argument(CONFIG_ARG)
                                        .executes(context -> displayCreator(context, UHCConfigArgument.get(context, CONFIG_ARG))))
                        )
                )
        );
    }

    private static int displayCreator(CommandContext<CommandSourceStack> context, Holder<UHCConfig> entry) {
        new CreateUHCGui(context.getSource().getPlayer(), entry, entry.value().clone()).open();
        return Command.SINGLE_SUCCESS;
    }
}
