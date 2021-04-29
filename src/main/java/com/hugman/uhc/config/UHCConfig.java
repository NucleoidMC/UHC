package com.hugman.uhc.config;

import com.hugman.uhc.game.module.Module;
import com.hugman.uhc.game.module.Modules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

import java.util.Collections;
import java.util.List;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
			Modules.CODEC.listOf().optionalFieldOf("modules", Collections.emptyList()).forGetter(config -> config.modules)
	).apply(instance, UHCConfig::new));

	private final PlayerConfig playerConfig;
	private final UHCMapConfig mapConfig;
	private final List<Module> modules;

	public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig, List<Module> modules) {
		this.playerConfig = players;
		this.mapConfig = mapConfig;
		this.modules = modules;
	}

	public PlayerConfig getPlayerConfig() {
		return playerConfig;
	}

	public UHCMapConfig getMapConfig() {
		return mapConfig;
	}

	public List<Module> getModifiers() {
		return modules;
	}
}
