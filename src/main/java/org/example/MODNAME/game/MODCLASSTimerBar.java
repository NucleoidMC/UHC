package org.example.MODNAME.game;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public final class MODCLASSTimerBar implements AutoCloseable {
    private final ServerBossBar bar;

    public MODCLASSTimerBar() {
        LiteralText title = new LiteralText("Waiting for the game to start...");

        this.bar = new ServerBossBar(title, BossBar.Color.GREEN, BossBar.Style.NOTCHED_10);
        this.bar.setDarkenSky(false);
        this.bar.setDragonMusic(false);
        this.bar.setThickenFog(false);
    }

    public void update(long ticksUntilEnd, long totalTicksUntilEnd) {
        if (ticksUntilEnd % 20 == 0) {
            this.bar.setName(this.getText(ticksUntilEnd));
            this.bar.setPercent((float) ticksUntilEnd / totalTicksUntilEnd);
        }
    }

    public void addPlayer(ServerPlayerEntity player) {
        this.bar.addPlayer(player);
    }

    private Text getText(long ticksUntilEnd) {
        long secondsUntilEnd = ticksUntilEnd / 20;

        long minutes = secondsUntilEnd / 60;
        long seconds = secondsUntilEnd % 60;
        String time = String.format("%02d:%02d left", minutes, seconds);

        return new LiteralText(time);
    }

    @Override
    public void close() {
        this.bar.clearPlayers();
        this.bar.setVisible(false);
    }
}
