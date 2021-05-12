package com.hugman.uhc.game;

import com.hugman.uhc.util.TickUtil;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public final class UHCBar {
	private final BossBarWidget widget;
	private final GameSpace gameSpace;
	private String name;
	private String message;
	private BossBar.Color color;
	private long totalTicks = 0;
	private boolean canTick = true;

	// TODO: work on sidebar
	private UHCBar(BossBarWidget widget, GameSpace gameSpace) {
		this.widget = widget;
		this.gameSpace = gameSpace;
	}

	public static UHCBar create(GlobalWidgets widgets, GameSpace gameSpace) {
		return new UHCBar(widgets.addBossBar(new TranslatableText("game.uhc.uhc"), BossBar.Color.BLUE, BossBar.Style.PROGRESS), gameSpace);
	}

	public void set(String name, long totalTicks, BossBar.Color color) {
		this.name = name + ".countdown_bar";
		this.message = name + ".countdown_text";
		this.totalTicks = totalTicks;
		this.color = color;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setFull(String translation) {
		this.name = translation;

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

			BossBar.Color newColor = this.color;
			if(seconds <= 5 || seconds == 10 || seconds == 15 || seconds == 30 || seconds == 60 || seconds == 150 || seconds == 300 || seconds == 600 || seconds == 900 || seconds == 1800) {
				sendMessage(seconds);
				newColor = BossBar.Color.RED;
			}
			this.widget.setTitle(new TranslatableText(name, TickUtil.format(ticks)));
			this.widget.setStyle(newColor, BossBar.Style.NOTCHED_10);
			this.widget.setProgress((float) seconds / totalSeconds);
		}
	}

	private void sendMessage(long seconds) {
		float pitch = seconds == 0 ? 1.5F : 1.0F;
		gameSpace.getPlayers().forEach(entity -> {
			if(this.message != null && seconds != 0) {
				entity.sendMessage(new TranslatableText(this.message, TickUtil.formatPretty(seconds * 20).formatted(Formatting.RED)).formatted(Formatting.GOLD), false);
			}
			entity.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, pitch);
		});
	}
}
