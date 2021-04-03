package com.hugman.uhc.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import com.hugman.uhc.game.map.UHCMapConfig;

public class UHCConfig {
    public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, UHCConfig::new));

    public final PlayerConfig playerConfig;
    public final UHCMapConfig mapConfig;
    public final int timeLimitSecs;

    public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig, int timeLimitSecs) {
        this.playerConfig = players;
        this.mapConfig = mapConfig;
        this.timeLimitSecs = timeLimitSecs;
    }
}
