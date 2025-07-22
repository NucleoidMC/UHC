package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class PreviousableGui extends SimpleGui {
    @Nullable
    private final GuiInterface previousUi;

    public PreviousableGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
        this.previousUi = GuiHelpers.getCurrentGui(player);
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.previousUi != null) {
            this.previousUi.open();
        }
    }
}
