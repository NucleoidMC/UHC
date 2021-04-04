package com.hugman.uhc.game;

public class UHCSizeLogic {
	private final int playerAmount;

	public UHCSizeLogic(int playerAmount) {
		this.playerAmount = playerAmount;
	}

	public int getMapMaxSize() {
		return 400 + (25 * playerAmount);
	}

	public int getMapMinSize() {
		return 10 * playerAmount;
	}

	public long getSafeSeconds() {
		return (30L + playerAmount * 5L) * 60L;
	}

	public long getShrinkingSeconds() {
		return (20L + playerAmount) * 15L;
	}
}
