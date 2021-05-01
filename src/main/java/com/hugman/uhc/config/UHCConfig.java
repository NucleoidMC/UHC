package com.hugman.uhc.config;

import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.Modules;
import com.hugman.uhc.module.piece.ModulePiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
			UHCTimeConfig.CODEC.fieldOf("time").forGetter(config -> config.timeConfig),
			Identifier.CODEC.listOf().optionalFieldOf("modules", Collections.emptyList()).forGetter(config -> config.modulesIds)
	).apply(instance, UHCConfig::new));

	private final PlayerConfig playerConfig;
	private final UHCMapConfig mapConfig;
	private final UHCTimeConfig timeConfig;
	private final List<Identifier> modulesIds;
	private final List<Module> modules;
	private final List<ModulePiece> modulesPieces;

	public UHCConfig(PlayerConfig players, UHCMapConfig mapConfig, UHCTimeConfig timeConfig, List<Identifier> modulesIds) {
		this.playerConfig = players;
		this.mapConfig = mapConfig;
		this.timeConfig = timeConfig;
		this.modulesIds = modulesIds;
		this.modules = modulesIds.stream().map(Modules::get).collect(Collectors.toList());
		this.modulesPieces = new ArrayList<>();
		modules.forEach(module -> modulesPieces.addAll(module.getPieces()));
	}

	public PlayerConfig getPlayerConfig() {
		return playerConfig;
	}

	public UHCMapConfig getMapConfig() {
		return mapConfig;
	}

	public UHCTimeConfig getTimeConfig() {
		return timeConfig;
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<ModulePiece> getModulesPieces() {
		return modulesPieces;
	}
}
