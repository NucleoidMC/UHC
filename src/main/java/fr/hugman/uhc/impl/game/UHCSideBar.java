package fr.hugman.uhc.impl.game;

import fr.hugman.uhc.api.util.TickUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.SidebarWidget;

public record UHCSideBar(SidebarWidget sidebarWidget) {
    public static UHCSideBar create(GlobalWidgets widgets, GameSpace gameSpace) {
        var name = gameSpace.getMetadata().sourceConfig().value().name();
        name = name != null ? name : Component.translatable("game.uhc");
        return new UHCSideBar(widgets.addSidebar(name.copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)));
    }

    public void update(long ticks, int worldSize, UHCPlayerManager playerManager) {
        sidebarWidget.set(content -> {
            content.add(Component.literal(""));
            content.add(Component.translatable("text.uhc.players", Component.literal(String.valueOf(playerManager.aliveCount())).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.WHITE));
            //TODO: write kills
            content.add(Component.literal(""));
            content.add(Component.translatable("text.uhc.world", Component.literal(worldSize + "x" + worldSize).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.WHITE));
            content.add(Component.literal(""));
            content.add(Component.translatable("text.uhc.time", TickUtil.format(ticks).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.WHITE));
        });
    }
}
