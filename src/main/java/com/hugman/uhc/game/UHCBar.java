package com.hugman.uhc.game;

import com.hugman.uhc.util.TimeUtil;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public final class UHCBar {
	private final BossBarWidget widget;
	private final GameSpace gameSpace;

	private UHCBar(BossBarWidget widget, GameSpace gameSpace) {
		this.widget = widget;
		this.gameSpace = gameSpace;
	}

	public static UHCBar create(GlobalWidgets widgets, GameSpace gameSpace) {
		return new UHCBar(widgets.addBossBar(new TranslatableText("game.uhc.uhc"), BossBar.Color.BLUE, BossBar.Style.PROGRESS), gameSpace);
	}

	public void tickUntilDrop(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 5, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		if(TimeUtil.blink(seconds, 5, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.dropping_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickUntilVulnerable(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 5, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		if(TimeUtil.blink(seconds, 5, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.YELLOW, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.vulnerable_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickUntilPvp(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 15, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		if(TimeUtil.blink(seconds, 15, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.pvp_enabled_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickUntilTp(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.tp_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickUntilShrinkStart(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);


		if(TimeUtil.blink(seconds, 10, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		if(TimeUtil.blink(seconds, 10, 2)) {
			this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		}
		else {
			this.widget.setStyle(BossBar.Color.BLUE, BossBar.Style.PROGRESS);
		}
		this.widget.setTitle(new TranslatableText("text.uhc.bar.shrink_start_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void tickUntilShrinkFinish(long ticks, long totalTicks) {
		long seconds = TimeUtil.asSeconds(ticks);
		long totalSeconds = TimeUtil.asSeconds(totalTicks);

		if(TimeUtil.blink(seconds, 10, 1)) {
			if(ticks % 20 == 0) {
				float pitch = seconds == 1 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
		}
		this.widget.setStyle(BossBar.Color.RED, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.shrink_finish_in", TimeUtil.format(ticks)));
		this.widget.setProgress((float) seconds / totalSeconds);
	}

	public void setDeathmatch() {
		this.widget.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.widget.setTitle(new TranslatableText("text.uhc.bar.deathmatch"));
		this.widget.setProgress(1.0f);
	}

	public void end() {
		this.widget.close();
	}
}
