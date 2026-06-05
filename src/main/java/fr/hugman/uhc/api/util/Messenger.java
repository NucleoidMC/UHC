package fr.hugman.uhc.api.util;

import fr.hugman.uhc.impl.game.ModuleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import fr.hugman.uhc.api.module.UHCModule;
import xyz.nucleoid.plasmid.api.game.GameSpacePlayers;

/**
 * Sends messages to players in a game.
 */
public class Messenger {
    public static final String SYMBOL_SKULL = "☠";
    public static final String SYMBOL_MODULE = "✨";
    public static final String SYMBOL_SHIELD = "🛡";
    public static final String SYMBOL_SWORD = "🗡";

    private final GameSpacePlayers players;

    public Messenger(GameSpacePlayers players) {
        this.players = players;
    }

    public void sound(SoundEvent sound, float volume, float pitch) {
        players.playSound(sound, SoundSource.PLAYERS, volume, pitch);
    }

    public void info(String symbol, String s, Object... args) {
        players.sendMessage(build(symbol, s, ChatFormatting.YELLOW, args));
    }

    public void info(String s, Object... args) {
        players.sendMessage(build(s, ChatFormatting.YELLOW, args));
    }

    public void danger(String symbol, String s, Object... args) {
        players.sendMessage(build(symbol, s, ChatFormatting.RED, args));
    }

    public void danger(String s, Object... args) {
        players.sendMessage(build(s, ChatFormatting.RED, args));
    }

    public void elimination(ServerPlayer player) {
        players.sendMessage(buildElimination(player));
        players.playSound(SoundEvents.WITHER_SPAWN);
    }

    public void death(DamageSource source, ServerPlayer player) {
        players.sendMessage(buildDeath(source, player));
        players.playSound(SoundEvents.WITHER_SPAWN);
    }

    public void moduleAnnouncement(String message, Holder<UHCModule> module, ChatFormatting formatting) {
        players.sendMessage(buildModuleAnnouncement(message, module, formatting));
    }

    public void moduleList(ModuleManager moduleManager) {
        if (!moduleManager.isEmpty()) {
            players.sendMessage(buildModuleList(moduleManager));
            players.playSound(SoundEvents.ITEM_PICKUP);
        }
    }

    private static Component build(String symbol, String s, ChatFormatting f, Object... args) {
        return Component.literal(symbol).append(" ").append(Component.translatable(s, args)).withStyle(f);
    }

    private static Component build(String s, ChatFormatting f, Object... args) {
        return Component.translatable(s, args).withStyle(f);
    }

    private static Component buildDeath(DamageSource source, ServerPlayer player) {
        return Component.literal("\n").append(SYMBOL_SKULL).append(" ").append(source.getLocalizedDeathMessage(player).copy()).append("!\n").withStyle(ChatFormatting.DARK_RED);
    }

    private static Component buildElimination(ServerPlayer player) {
        return Component.literal("\n").append(SYMBOL_SKULL).append(" ").append(Component.translatable("text.uhc.player_eliminated", player.getDisplayName())).append("\n").withStyle(ChatFormatting.DARK_RED);
    }

    private static Component buildModuleAnnouncement(String message, Holder<UHCModule> module, ChatFormatting formatting) {
        return Component.literal("\n\n").append(SYMBOL_MODULE).append(" ").append(Component.translatable(message, moduleSnippet(module.value())).withStyle(formatting)).append("\n\n");
    }

    private static Component buildModuleList(ModuleManager manager) {
        var text = Component.literal("\n").append(Component.translatable("text.uhc.enabled_modules").withStyle(ChatFormatting.GOLD));
        manager.forEach(module -> text.append(Component.literal("\n  - ").withStyle(ChatFormatting.WHITE)).append(moduleSnippet(module)));
        text.append("\n");
        return text;
    }


    private static Component moduleSnippet(UHCModule module) {
        var style = Style.EMPTY;
        if (module.description().isPresent()) {
            style = style.withHoverEvent(new HoverEvent.ShowText(module.description().get().copy()));
        }
        return ComponentUtils.wrapInSquareBrackets(module.name()).setStyle(style.withColor(module.color()));
    }
}
