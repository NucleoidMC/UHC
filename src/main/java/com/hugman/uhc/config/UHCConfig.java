package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

import java.util.Collections;
import java.util.List;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
			UHCChapterConfig.CODEC.fieldOf("chapters").forGetter(config -> config.timeConfig),
			Identifier.CODEC.listOf().optionalFieldOf("modules", Collections.emptyList()).forGetter(config -> config.modulesIds)
	).apply(instance, UHCConfig::new));

	private final PlayerConfig playerConfig;
	private final UHCMapConfig mapConfig;
	private final UHCChapterConfig timeConfig;
	private final List<Identifier> modulesIds;

	public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig, UHCChapterConfig timeConfig, List<Identifier> modulesIds) {
		this.playerConfig = players;
		this.mapConfig = mapConfig;
		this.timeConfig = timeConfig;
		this.modulesIds = modulesIds;
	}

	public PlayerConfig getPlayerConfig() {
		return playerConfig;
	}

	public UHCMapConfig getMapConfig() {
		return mapConfig;
	}

	public UHCChapterConfig getTimeConfig() {
		return timeConfig;
	}

	public List<Identifier> getModulesIds() {
		return modulesIds;
	}
}
