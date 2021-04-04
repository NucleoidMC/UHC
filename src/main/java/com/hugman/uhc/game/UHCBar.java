package com.hugman.uhc.game;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public final class UHCBar {
	private final BossBarWidget widget;

	private UHCBar(BossBarWidget widget) {
		this.widget = widget;
	}

	public static UHCBar create(GlobalWidgets widgets) {
		return new UHCBar(widgets.addBossBar(new TranslatableText("game.uhc.uhc"), BossBar.Color.BLUE, BossBar.Style.PROGRESS));
	}

	private static String formatTime(long ticksUntil) {
		long secondsUntil = ticksUntil / 20;

		long minutes = secondsUntil / 60;
		long seconds = secondsUntil % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	public void tickStarting(long ticks, long totalTicks) {
		String time = formatTime(ticks);

		this.widget.setTitle(new TranslatableText("text.uhc.bar.starting", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickSafe(long ticks, long totalTicks) {
		String time = formatTime(ticks);

		this.widget.setTitle(new TranslatableText("text.uhc.bar.worldborder_safe", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickShrinking(long ticks, long totalTicks) {
		String time = formatTime(ticks);

		this.widget.setTitle(new TranslatableText("text.uhc.bar.worldborder_shrinking", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void setFinished() {
		this.widget.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.fight"));
		this.widget.setProgress(1.0f);
	}

	public void setActive() {
		this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
	}
}
