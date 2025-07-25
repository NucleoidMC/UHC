package fr.hugman.uhc.api.gui.widget;

import eu.pb4.sgui.api.SlotHolder;
import net.minecraft.server.network.ServerPlayerEntity;

public class GuiWidget {
    protected ServerPlayerEntity player;
    protected SlotHolder gui;

    public GuiWidget(ServerPlayerEntity player, SlotHolder gui) {
        this.player = player;
        this.gui = gui;
    }
}
