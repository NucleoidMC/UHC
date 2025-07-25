package fr.hugman.uhc.api.game;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public enum UHCGameTeamSize {
    SOLO("solo", Items.RED_CANDLE, 1, 2, 8),
    DUOS("duos", Items.LIME_CANDLE, 2, 4, 16),
    TRIOS("trios", Items.PINK_CANDLE, 3, 6, 24),
    SQUADS("squads", Items.BLUE_CANDLE, 4, 8, 32);

    private final String name;
    private final Item item;
    private final int teamsize;
    private final int minPlayers;
    private final int thresholdPlayers;

    UHCGameTeamSize(String name, Item item, int teamsize, int minPlayers, int thresholdPlayers) {
        this.name = name;
        this.item = item;
        this.teamsize = teamsize;
        this.minPlayers = minPlayers;
        this.thresholdPlayers = thresholdPlayers;
    }

    public String getName() {
        return name;
    }

    public int getTeamsize() {
        return teamsize;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getThresholdPlayers() {
        return thresholdPlayers;
    }

    public GuiElementBuilder createElement() {
        return new GuiElementBuilder(this.item).setName(Text.translatable("mode." + name));
    }
}
