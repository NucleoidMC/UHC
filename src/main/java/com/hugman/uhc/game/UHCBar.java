package com.hugman.uhc.game;

import com.hugman.uhc.util.Time;
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

	public void tickCages(long ticks, long totalTicks) {
		Time time = new Time(ticks);

		this.widget.setStyle(BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.cages", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickInvulnerable(long ticks, long totalTicks) {
		Time time = new Time(ticks);

		this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.invulnerable", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickPeaceful(long ticks, long totalTicks) {
		Time time = new Time(ticks);

		this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.peaceful", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickWild(long ticks, long totalTicks) {
		Time time = new Time(ticks);

		this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.wild", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickShrinking(long ticks, long totalTicks) {
		Time time = new Time(ticks);

		this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.shrinking", time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void setDeathmatch() {
		this.widget.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.deathmatch"));
		this.widget.setProgress(1.0f);
	}
}
