package fr.hugman.uhc.api.game;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum UHCGameTeamSize {
    SOLO("solo", Items.DYED_CANDLE.red(), 1, 2, 8),
    DUOS("duos", Items.DYED_CANDLE.lime(), 2, 4, 16),
    TRIOS("trios", Items.DYED_CANDLE.pink(), 3, 6, 24),
    SQUADS("squads", Items.DYED_CANDLE.blue(), 4, 8, 32);

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

    public Item getItem() {
        return item;
    }

    public Component getDisplayName() {
        return Component.translatable("mode." + name);
    }

    public UHCGameTeamSize next(boolean forward) {
        var values = values();
        return values[Math.floorMod(this.ordinal() + (forward ? 1 : -1), values.length)];
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
}
