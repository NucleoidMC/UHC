package com.hugman.uhc.config;

import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

import java.util.ArrayList;
import java.util.List;

public record UHCGameConfig(
        WaitingLobbyConfig players,
        int teamSize,
        RegistryEntry<UHCConfig> uhcConfig
) {
    public static final MapCodec<UHCGameConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(UHCGameConfig::players),
            Codecs.POSITIVE_INT.optionalFieldOf("team_size", 1).forGetter(UHCGameConfig::teamSize),
            UHCConfig.ENTRY_CODEC.fieldOf("config").forGetter(UHCGameConfig::uhcConfig)
    ).apply(instance, UHCGameConfig::new));

    public List<Modifier> getModifiers() {
        List<Modifier> modifiers = new ArrayList<>();
        for (var moduleEntry : uhcConfig.value().modules()) {
            modifiers.addAll(moduleEntry.value().modifiers());
        }
        return modifiers;
    }

    public <V extends Modifier> List<V> getModifiers(ModifierType<V> type) {
        //TODO: cache modules so it's quicker to sort by type
        List<V> modifiers = new ArrayList<>();
        for (var moduleEntry : uhcConfig.value().modules()) {
            for (Modifier modifier : moduleEntry.value().modifiers()) {
                if (modifier.getType() == type) {
                    modifiers.add((V) modifier);
                }
            }
        }
        return modifiers;
    }
}
