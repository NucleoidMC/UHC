package com.hugman.uhc.game;

import com.hugman.uhc.util.TickUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

public final class UHCSideBar {
	private final SidebarWidget sidebarWidget;
	private final GameSpace gameSpace;

	private UHCSideBar(SidebarWidget sidebarWidget, GameSpace gameSpace) {
		this.sidebarWidget = sidebarWidget;
		this.gameSpace = gameSpace;
	}

	public static UHCSideBar create(GlobalWidgets widgets, GameSpace gameSpace) {
		return new UHCSideBar(widgets.addSidebar(gameSpace.getGameConfig().getNameText().copy().formatted(Formatting.BOLD, Formatting.GOLD)), gameSpace);
	}

	public void update(long ticks) {
		sidebarWidget.set(content -> {
			long count = gameSpace.getPlayers().stream().filter(entity -> entity.interactionManager.getGameMode().isSurvivalLike()).count();

			content.writeLine("");
			content.writeFormattedTranslated(Formatting.GRAY, "text.uhc.players_left", new LiteralText(String.valueOf(count)).formatted(Formatting.WHITE));
			//TODO: write kills
			content.writeLine("");
			content.writeFormattedTranslated(Formatting.GRAY, "text.uhc.time", new LiteralText(TickUtil.format(ticks).asString()).formatted(Formatting.WHITE));
		});
	}
}
