package com.hugman.uhc.game;

import com.hugman.uhc.game.phase.UHCActive;
import com.hugman.uhc.util.TickUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.widget.SidebarWidget;

public class UHCSideBar {
	private final SidebarWidget sidebarWidget;
	private final UHCActive active;

	private UHCSideBar(SidebarWidget sidebarWidget, UHCActive active) {
		this.sidebarWidget = sidebarWidget;
		this.active = active;
	}

	public static UHCSideBar create(GlobalWidgets widgets, UHCActive active) {
		return new UHCSideBar(widgets.addSidebar(active.gameSpace.getSourceConfig().getName().copy().formatted(Formatting.BOLD, Formatting.GOLD)), active);
	}

	public void update(long ticks, int worldSize) {
		sidebarWidget.set(content -> {
			long count = active.getParticipants().size();

			content.add(new LiteralText(""));
			content.add(new TranslatableText("text.uhc.players", new LiteralText(String.valueOf(count)).formatted(Formatting.GREEN)).formatted(Formatting.WHITE));
			//TODO: write kills
			content.add(new LiteralText(""));
			content.add(new TranslatableText("text.uhc.world", new LiteralText(worldSize + "x" + worldSize).formatted(Formatting.GREEN)).formatted(Formatting.WHITE));
			content.add(new LiteralText(""));
			content.add(new TranslatableText("text.uhc.time", new LiteralText(TickUtil.format(ticks).asString()).formatted(Formatting.GREEN)).formatted(Formatting.WHITE));
		});
	}
}
