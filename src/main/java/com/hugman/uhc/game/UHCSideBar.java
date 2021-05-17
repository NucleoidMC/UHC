package com.hugman.uhc.game;

import com.hugman.uhc.game.phase.UHCActive;
import com.hugman.uhc.util.TickUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

public class UHCSideBar {
	private final SidebarWidget sidebarWidget;
	private final UHCActive active;

	private UHCSideBar(SidebarWidget sidebarWidget, UHCActive active) {
		this.sidebarWidget = sidebarWidget;
		this.active = active;
	}

	public static UHCSideBar create(GlobalWidgets widgets, UHCActive active) {
		return new UHCSideBar(widgets.addSidebar(active.gameSpace.getGameConfig().getNameText().copy().formatted(Formatting.BOLD, Formatting.GOLD)), active);
	}

	public void update(long ticks, int worldSize) {
		sidebarWidget.set(content -> {
			long count = active.getParticipants().size();

			content.writeLine("");
			content.writeFormattedTranslated(Formatting.WHITE, "text.uhc.players", new LiteralText(String.valueOf(count)).formatted(Formatting.GREEN));
			//TODO: write kills
			content.writeLine("");
			content.writeFormattedTranslated(Formatting.WHITE, "text.uhc.world", new LiteralText(worldSize + "x" + worldSize).formatted(Formatting.GREEN));
			content.writeLine("");
			content.writeFormattedTranslated(Formatting.WHITE, "text.uhc.time", new LiteralText(TickUtil.format(ticks).asString()).formatted(Formatting.GREEN));
		});
	}
}
