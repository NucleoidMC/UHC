package com.hugman.uhc.game;

import com.hugman.uhc.util.TickUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.widget.SidebarWidget;

public record UHCSideBar(SidebarWidget sidebarWidget) {
	public static UHCSideBar create(GlobalWidgets widgets, GameSpace gameSpace) {
		return new UHCSideBar(widgets.addSidebar(gameSpace.getSourceConfig().getName().copy().formatted(Formatting.BOLD, Formatting.GOLD)));
	}

	public void update(long ticks, int worldSize, Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participantMap) {
		sidebarWidget.set(content -> {
			long count = participantMap.values().stream().filter(participant -> !participant.isEliminated()).count();

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
