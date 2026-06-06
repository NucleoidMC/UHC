package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.SguiUtils;
import eu.pb4.sgui.api.gui.GuiLike;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PreviousableGui extends SimpleGui {
    @Nullable
    protected final GuiLike previousUi;

    public PreviousableGui(MenuType<?> type, ServerPlayer player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
        this.previousUi = SguiUtils.getCurrentGui(player);
    }

    @Override
    public void afterRemoval() {
        super.afterRemoval();
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
