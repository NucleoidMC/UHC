package com.hugman.uhc.game;

import com.hugman.uhc.util.TimeUtil;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.sql.Time;

public final class UHCBar {
	private final BossBarWidget widget;

	private UHCBar(BossBarWidget widget) {
		this.widget = widget;
	}

	public static UHCBar create(GlobalWidgets widgets) {
		return new UHCBar(widgets.addBossBar(new TranslatableText("game.uhc.uhc"), BossBar.Color.BLUE, BossBar.Style.PROGRESS));
	}

	public void tickCages(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.cages", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickInvulnerable(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.YELLOW, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.invulnerable", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickPeaceful(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.peaceful", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickWild(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.wild", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickShrinking(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.shrinking", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void setDeathmatch() {
		this.widget.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.deathmatch"));
		this.widget.setProgress(1.0f);
	}
}
