package fr.hugman.uhc.api.gui.widget;

import eu.pb4.sgui.api.SlotHolder;
import net.minecraft.server.level.ServerPlayer;

public class GuiWidget {
    protected ServerPlayer player;
    protected SlotHolder gui;

    public GuiWidget(ServerPlayer player, SlotHolder gui) {
        this.player = player;
        this.gui = gui;
    }
}
