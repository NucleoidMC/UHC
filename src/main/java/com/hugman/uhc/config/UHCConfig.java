package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig)
	).apply(instance, UHCConfig::new));

	private final PlayerConfig playerConfig;
	private final UHCMapConfig mapConfig;

	public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig) {
		this.playerConfig = players;
		this.mapConfig = mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return playerConfig;
	}

	public UHCMapConfig getMapConfig() {
		return mapConfig;
	}
}
