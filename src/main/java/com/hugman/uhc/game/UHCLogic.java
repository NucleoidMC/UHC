package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;

public class UHCLogic {
	private final UHCConfig config;
	private final float playerDose;

	public UHCLogic(UHCConfig config, int playerAmount) {
		this.config = config;
		this.playerDose = ((float)playerAmount / ((float)config.getPlayerConfig().getMaxPlayers() - (float)config.getPlayerConfig().getMinPlayers())) + (float)config.getPlayerConfig().getMinPlayers();;
	}

	public double getMapMaxSize() {
		return config.getMapConfig().getMaxSize().crossProduct(playerDose);
	}

	public double getMapMinSize() {
		return config.getMapConfig().getMinSize().crossProduct(playerDose);
	}

	public long getInCagesTime() {
		return (long) (config.getTimeConfig().getInCagesTime().crossProduct(playerDose) * 20L);
	}

	public long getInvulnerabilityTime() {
		return (long) (config.getTimeConfig().getInvulnerabilityTime().crossProduct(playerDose) * 20L);
	}

	public long getSetupTime() {
		return (long) (config.getTimeConfig().getSetupTime().crossProduct(playerDose) * 20L);
	}

	public long getShrinkingTime() {
		return (long) (config.getTimeConfig().getShrinkingTime().crossProduct(playerDose) * 20L);
	}
}
