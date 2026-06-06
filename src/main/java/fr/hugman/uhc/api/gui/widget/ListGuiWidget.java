package fr.hugman.uhc.api.gui.widget;

import eu.pb4.sgui.api.SlotHolder;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class ListGuiWidget<Object> extends GuiWidget {
    private final List<Object> list;
    private final Function<Object, GuiElementBuilder> elementBuilderProvider;
    private final int startingRow;
    private final int height;
    private final @Nullable Consumer<Object> clickConsumer;
    private int page = 0;

    public ListGuiWidget(
            SlotHolder gui,
            ServerPlayer player,
            List<Object> list,
            Function<Object, GuiElementBuilder> elementBuilderProvider,
            int startingRow,
            int height,
            @Nullable Consumer<Object> clickConsumer
    ) {
        super(player, gui);
        this.list = list;
        this.elementBuilderProvider = elementBuilderProvider;
        this.startingRow = startingRow;
        this.height = height;
        this.clickConsumer = clickConsumer;
    }

    public List<Object> getList() {
        return list;
    }

    public void refreshDisplay() {
        int maxModulesPerPage = height * 7;
        int size = list.size();
        if (size > 0 && size <= page * maxModulesPerPage) {
            this.page = (size - 1) / maxModulesPerPage;
            refreshDisplay();
        }

        int row = startingRow;
        int col = 1;
        for (int i = 0; i < maxModulesPerPage; i++) {
            int slot = row * 9 + col;
            int elementIndex = i + page * maxModulesPerPage;
            if (elementIndex >= size) {
                gui.setSlot(slot, SimpleGuiElement.EMPTY);
            } else {
                var value = list.get(elementIndex);
                var uiElement = this.elementBuilderProvider.apply(value);
                if (clickConsumer != null) {
                    uiElement.setCallback((index, clickType, action, guiInterface) -> {
                        UHCConfigGuiElements.playClickSound(player);
                        clickConsumer.accept(value);
                        refreshDisplay();
                    });
                }
                gui.setSlot(slot, uiElement);
            }

            col++;
            if (col >= 8) {
                col = 1;
                row++;
            }
        }

        boolean hasNextPage = (page + 1) * maxModulesPerPage < list.size();
        boolean hasPreviousPage = page > 0;
        int middleRow = startingRow * 9 + (Mth.floor(height / 2.0f) * 9);
        if (hasPreviousPage) {
            gui.setSlot(middleRow, UHCConfigGuiElements.previousPage(player).setCallback((index, type, action, gui) -> {
                UHCConfigGuiElements.playClickSound(player);
                page = page - 1;
                refreshDisplay();
            }));
        } else {
            gui.setSlot(middleRow, SimpleGuiElement.EMPTY);
        }
        if (hasNextPage) {
            gui.setSlot(middleRow + 8, UHCConfigGuiElements.nextPage(player).setCallback((index, type, action, gui) -> {
                UHCConfigGuiElements.playClickSound(player);
                page = page + 1;
                refreshDisplay();
            }));
        } else {
            gui.setSlot(middleRow + 8, SimpleGuiElement.EMPTY);
        }
    }
}