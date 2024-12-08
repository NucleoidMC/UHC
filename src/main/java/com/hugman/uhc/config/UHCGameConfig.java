package com.hugman.uhc.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

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
}
