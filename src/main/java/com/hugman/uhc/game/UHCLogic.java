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
		return config.getMapConfig().getStartSize().crossProduct(playerDose);
	}

	public double getEndMapSize() {
		return config.getMapConfig().getEndSize().crossProduct(playerDose);
	}

	public long getInCagesTime() {
		return (long) (config.getTimeConfig().getInCagesTime().crossProduct(playerDose) * 20L);
	}

	public long getInvulnerabilityTime() {
		return (long) (config.getTimeConfig().getInvulnerabilityTime().crossProduct(playerDose) * 20L);
	}

	public long getWarmupTime() {
		return (long) (config.getTimeConfig().getWarmupTime().crossProduct(playerDose) * 20L);
	}

	public long getDeathmatchTime() {
		return (long) (config.getTimeConfig().getDeathmatchTime().crossProduct(playerDose) * 20L);
	}

	public long getShrinkingTime() {
		return (long) ((getStartMapSize() - getEndMapSize()) / config.getMapConfig().getShrinkingSpeed() * 10L);
	}
}
