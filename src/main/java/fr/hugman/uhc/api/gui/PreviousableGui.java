package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PreviousableGui extends SimpleGui {
    @Nullable
    protected final GuiInterface previousUi;

    public PreviousableGui(MenuType<?> type, ServerPlayer player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
        this.previousUi = GuiHelpers.getCurrentGui(player);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.previousUi != null) {
            this.previousUi.open();
        }
    }

    protected void addBackButton() {
        if (previousUi != null) {
            setSlot(0, UHCConfigGuiElements.back(player, this::close));
        }
    }
}
