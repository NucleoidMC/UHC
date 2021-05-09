package com.hugman.uhc.game;

import com.hugman.uhc.util.TickUtil;
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
	private String translation = "game.uhc.uhc";
	private long totalTicks = 0;
	private boolean canTick = true;

	private UHCBar(BossBarWidget widget, GameSpace gameSpace) {
		this.widget = widget;
		this.gameSpace = gameSpace;
	}

	public static UHCBar create(GlobalWidgets widgets, GameSpace gameSpace) {
		return new UHCBar(widgets.addBossBar(new TranslatableText("game.uhc.uhc"), BossBar.Color.BLUE, BossBar.Style.PROGRESS), gameSpace);
	}

	public void set(String translation, long totalTicks, BossBar.Color color) {
		this.translation = translation;
		this.totalTicks = totalTicks;
		this.widget.setStyle(color, BossBar.Style.NOTCHED_10);
	}

	public void setFull(String translation) {
		this.translation = translation;

		this.widget.setTitle(new TranslatableText(translation));
		this.widget.setStyle(BossBar.Color.GREEN, BossBar.Style.PROGRESS);
		this.widget.setProgress(1.0f);
	}

	public void close() {
		this.widget.close();
		this.canTick = false;
	}

	public void tick(long ticks) {
		if(ticks % 20 == 0 && canTick) {
			long seconds = TickUtil.asSeconds(ticks);
			long totalSeconds = TickUtil.asSeconds(totalTicks);

			if(TickUtil.asSeconds(ticks) <= 10) {
				float pitch = seconds == 0 ? 1.5F : 1.0F;
				gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
			}
			this.widget.setTitle(new TranslatableText(translation, TickUtil.format(ticks)));
			this.widget.setProgress((float) seconds / totalSeconds);
		}
	}
}
