package com.hugman.uhc.game;

public class UHCParticipant {
	private int kills = 0;

	public UHCParticipant() {
		this.kills = 0;
	}

	public void addKill() {
		this.kills++;
	}

	public int getKills() {
		return this.kills;
	}
}
