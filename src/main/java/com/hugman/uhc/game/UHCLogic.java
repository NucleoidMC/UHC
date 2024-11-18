package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCGameConfig;

public class UHCLogic {
    private static final int HIGH_PLAYER_COUNT = 100;

    private final UHCGameConfig config;
    private final float playerDose;

    public UHCLogic(UHCGameConfig config, int playerAmount) {
        this.config = config;
        this.playerDose = (playerAmount - (float) config.players().minPlayers()) / ((float) Math.max(config.players().minPlayers(), HIGH_PLAYER_COUNT) - (float) config.players().minPlayers());
    }

    public double getStartMapSize() {
        return config.uhcConfig().value().mapConfig().startSize().crossProduct(playerDose);
    }

    public double getEndMapSize() {
        return config.uhcConfig().value().mapConfig().endSize().crossProduct(playerDose);
    }

    public long getInCagesTime() {
        return (long) (config.uhcConfig().value().chapterConfig().cages() * 20L);
    }

    public long getInvulnerabilityTime() {
        return (long) (config.uhcConfig().value().chapterConfig().invulnerability() * 20L);
    }

    public long getWarmupTime() {
        return (long) (config.uhcConfig().value().chapterConfig().warmup() * 20L);
    }

    public long getDeathmatchTime() {
        return (long) (config.uhcConfig().value().chapterConfig().deathmatch() * 20L);
    }

    public long getShrinkingTime() {
        return (long) ((getStartMapSize() - getEndMapSize()) / config.uhcConfig().value().mapConfig().shrinkingSpeed() * 10L);
    }
}
