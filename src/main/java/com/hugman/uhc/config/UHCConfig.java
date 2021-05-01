package com.hugman.uhc.config;

import com.hugman.uhc.module.ConfiguredModule;
import com.hugman.uhc.module.ConfiguredModules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
			Identifier.CODEC.listOf().optionalFieldOf("configured_modules", Collections.emptyList()).forGetter(config -> config.configuredModulesIds)
	).apply(instance, UHCConfig::new));

	private final PlayerConfig playerConfig;
	private final UHCMapConfig mapConfig;
	private final List<Identifier> configuredModulesIds;

	public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig, List<Identifier> configuredModulesIds) {
		this.playerConfig = players;
		this.mapConfig = mapConfig;
		this.configuredModulesIds = configuredModulesIds;
	}

	public PlayerConfig getPlayerConfig() {
		return playerConfig;
	}

	public UHCMapConfig getMapConfig() {
		return mapConfig;
	}

	public List<Identifier> getConfiguredModulesIds() {
		return configuredModulesIds;
	}

	public List<ConfiguredModule> getConfiguredModules() {
		return getConfiguredModulesIds().stream().map(ConfiguredModules::get).collect(Collectors.toList());
	}
}
