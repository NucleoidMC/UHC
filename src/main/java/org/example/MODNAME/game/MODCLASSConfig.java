package org.example.MODNAME.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import org.example.MODNAME.game.map.MODCLASSMapConfig;

public class MODCLASSConfig {
    public static final Codec<MODCLASSConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            MODCLASSMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, MODCLASSConfig::new));

    public final PlayerConfig playerConfig;
    public final MODCLASSMapConfig mapConfig;
    public final int timeLimitSecs;

    public MODCLASSConfig(PlayerConfig players, MODCLASSMapConfig mapConfig, int timeLimitSecs) {
        this.playerConfig = players;
        this.mapConfig = mapConfig;
        this.timeLimitSecs = timeLimitSecs;
    }
}
