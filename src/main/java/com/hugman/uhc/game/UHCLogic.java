package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;

public class UHCLogic {
	private final UHCConfig config;
	private final float playerDose;

	public UHCLogic(UHCConfig config, int playerAmount) {
		this.config = config;
		this.playerDose = (playerAmount - (float) config.playerConfig().minPlayers()) / ((float) config.playerConfig().maxPlayers() - (float) config.playerConfig().minPlayers());
	}

	public double getStartMapSize() {
		return config.mapConfig().startSize().crossProduct(playerDose);
	}

	public double getEndMapSize() {
		return config.mapConfig().endSize().crossProduct(playerDose);
	}

	public long getInCagesTime() {
		return (long) (config.timeConfig().inCagesTime().crossProduct(playerDose) * 20L);
	}

	public long getInvulnerabilityTime() {
		return (long) (config.timeConfig().invulnerabilityTime().crossProduct(playerDose) * 20L);
	}

	public long getWarmupTime() {
		return (long) (config.timeConfig().warmupTime().crossProduct(playerDose) * 20L);
	}

	public long getDeathmatchTime() {
		return (long) (config.timeConfig().deathmatchTime().crossProduct(playerDose) * 20L);
	}

	public long getShrinkingTime() {
		return (long) ((getStartMapSize() - getEndMapSize()) / config.mapConfig().shrinkingSpeed() * 10L);
	}
}
