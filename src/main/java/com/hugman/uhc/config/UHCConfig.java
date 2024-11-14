package com.hugman.uhc.config;

import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntryList;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

import java.util.ArrayList;
import java.util.List;

public record UHCConfig(
        WaitingLobbyConfig players,
        int teamSize,
        UHCMapConfig mapConfig,
        UHCChapterConfig chapterConfig,
        RegistryEntryList<Module> modules
) {
    public static final MapCodec<UHCConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(UHCConfig::players),
            Codec.INT.fieldOf("team_size").forGetter(UHCConfig::teamSize),
            UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
            UHCChapterConfig.CODEC.fieldOf("chapters").forGetter(UHCConfig::chapterConfig),
            Module.LIST_CODEC.optionalFieldOf("modules", RegistryEntryList.of()).forGetter(UHCConfig::modules)
    ).apply(instance, UHCConfig::new));

    public List<Modifier> getModifiers() {
        List<Modifier> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            modifiers.addAll(moduleEntry.value().modifiers());
        }
        return modifiers;
    }

    public <V extends Modifier> List<V> getModifiers(ModifierType<V> type) {
        List<V> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            for (Modifier modifier : moduleEntry.value().modifiers()) {
                if (modifier.getType() == type) {
                    modifiers.add((V) modifier);
                }
            }
        }
        return modifiers;
    }
}
