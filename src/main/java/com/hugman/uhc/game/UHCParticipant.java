package com.hugman.uhc.game;

public class UHCParticipant {
    private int kills;
    private boolean eliminated;

    public UHCParticipant() {
        this.kills = 0;
        this.eliminated = false;
    }

    public void addKill() {
        this.kills++;
    }

    public int getKills() {
        return this.kills;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void eliminate() {
        this.eliminated = true;
    }
}
