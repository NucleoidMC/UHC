package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;

public class UHCLogic {
	private final UHCConfig config;
	private final float playerDose;

	public UHCLogic(UHCConfig config, int playerAmount) {
		this.config = config;
		this.playerDose = (playerAmount - (float) config.getPlayerConfig().minPlayers()) / ((float) config.getPlayerConfig().maxPlayers() - (float) config.getPlayerConfig().minPlayers());
	}

	public double getStartMapSize() {
		return config.getMapConfig().startSize().crossProduct(playerDose);
	}

	public double getEndMapSize() {
		return config.getMapConfig().endSize().crossProduct(playerDose);
	}

	public long getInCagesTime() {
		return (long) (config.getTimeConfig().inCagesTime().crossProduct(playerDose) * 20L);
	}

	public long getInvulnerabilityTime() {
		return (long) (config.getTimeConfig().invulnerabilityTime().crossProduct(playerDose) * 20L);
	}

	public long getWarmupTime() {
		return (long) (config.getTimeConfig().warmupTime().crossProduct(playerDose) * 20L);
	}

	public long getDeathmatchTime() {
		return (long) (config.getTimeConfig().deathmatchTime().crossProduct(playerDose) * 20L);
	}

	public long getShrinkingTime() {
		return (long) ((getStartMapSize() - getEndMapSize()) / config.getMapConfig().shrinkingSpeed() * 10L);
	}
}
