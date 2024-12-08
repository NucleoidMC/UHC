package com.hugman.text;

import com.hugman.uhc.game.ModuleManager;
import com.hugman.uhc.module.Module;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
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
        players.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
    }

    public void info(String symbol, String s, Object... args) {
        players.sendMessage(build(symbol, s, Formatting.YELLOW, args));
    }

    public void info(String s, Object... args) {
        players.sendMessage(build(s, Formatting.YELLOW, args));
    }

    public void danger(String symbol, String s, Object... args) {
        players.sendMessage(build(symbol, s, Formatting.RED, args));
    }

    public void danger(String s, Object... args) {
        players.sendMessage(build(s, Formatting.RED, args));
    }

    public void elimination(ServerPlayerEntity player) {
        players.sendMessage(buildElimination(player));
        players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
    }

    public void death(DamageSource source, ServerPlayerEntity player) {
        players.sendMessage(buildDeath(source, player));
        players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
    }

    public void moduleAnnouncement(String message, RegistryEntry<Module> module, Formatting formatting) {
        players.sendMessage(buildModuleAnnouncement(message, module, formatting));
    }

    public void moduleList(ModuleManager moduleManager) {
        if (!moduleManager.isEmpty()) {
            players.sendMessage(buildModuleList(moduleManager));
            players.playSound(SoundEvents.ENTITY_ITEM_PICKUP);
        }
    }

    private static Text build(String symbol, String s, Formatting f, Object... args) {
        return Text.literal(symbol).append(" ").append(Text.translatable(s, args)).formatted(f);
    }

    private static Text build(String s, Formatting f, Object... args) {
        return Text.translatable(s, args).formatted(f);
    }

    private static Text buildDeath(DamageSource source, ServerPlayerEntity player) {
        return Text.literal("\n").append(SYMBOL_SKULL).append(" ").append(source.getDeathMessage(player).copy()).append("!\n").formatted(Formatting.DARK_RED);
    }

    private static Text buildElimination(ServerPlayerEntity player) {
        return Text.literal("\n").append(SYMBOL_SKULL).append(" ").append(Text.translatable("text.uhc.player_eliminated", player.getDisplayName())).append("\n").formatted(Formatting.DARK_RED);
    }

    private static Text buildModuleAnnouncement(String message, RegistryEntry<Module> module, Formatting formatting) {
        return Text.literal("\n\n").append(SYMBOL_MODULE).append(" ").append(Text.translatable(message, moduleSnippet(module.value())).formatted(formatting)).append("\n\n");
    }

    private static Text buildModuleList(ModuleManager manager) {
        var text = Text.literal("\n").append(Text.translatable("text.uhc.enabled_modules").formatted(Formatting.GOLD));
        manager.forEach(module -> text.append(Text.literal("\n  - ").formatted(Formatting.WHITE)).append(moduleSnippet(module)));
        text.append("\n");
        return text;
    }


    private static Text moduleSnippet(Module module) {
        var style = Style.EMPTY;
        if (module.description().isPresent()) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, module.description().get().copy()));
        }
        return Texts.bracketed(module.name()).setStyle(style.withColor(module.color()));
    }
}
