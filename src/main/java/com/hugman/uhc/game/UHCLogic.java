package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;

public class UHCLogic {
    private static final int HIGH_PLAYER_COUNT = 100;

    private final UHCConfig config;
    private final float playerDose;

    public UHCLogic(UHCConfig config, int playerAmount) {
        this.config = config;
        this.playerDose = (playerAmount - (float) config.players().minPlayers()) / ((float) Math.max(config.players().minPlayers(), HIGH_PLAYER_COUNT) - (float) config.players().minPlayers());
    }

    public double getStartMapSize() {
        return config.mapConfig().startSize().crossProduct(playerDose);
    }

    public double getEndMapSize() {
        return config.mapConfig().endSize().crossProduct(playerDose);
    }

    public long getInCagesTime() {
        return (long) (config.chapterConfig().inCagesTime().crossProduct(playerDose) * 20L);
    }

    public long getInvulnerabilityTime() {
        return (long) (config.chapterConfig().invulnerabilityTime().crossProduct(playerDose) * 20L);
    }

    public long getWarmupTime() {
        return (long) (config.chapterConfig().warmupTime().crossProduct(playerDose) * 20L);
    }

    public long getDeathmatchTime() {
        return (long) (config.chapterConfig().deathmatchTime().crossProduct(playerDose) * 20L);
    }

    public long getShrinkingTime() {
        return (long) ((getStartMapSize() - getEndMapSize()) / config.mapConfig().shrinkingSpeed() * 10L);
    }
}
