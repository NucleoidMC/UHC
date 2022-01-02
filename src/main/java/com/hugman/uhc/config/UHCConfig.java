package com.hugman.uhc.config;

import com.hugman.uhc.UHCRegistries;
import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieceType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					PlayerConfig.CODEC.fieldOf("players").forGetter(UHCConfig::playerConfig),
					Codec.INT.fieldOf("team_size").forGetter(UHCConfig::teamSize),
					UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
					UHCChapterConfig.CODEC.fieldOf("chapters").forGetter(UHCConfig::timeConfig),
					UHCRegistries.MODULES.listOf().optionalFieldOf("modules", Collections.emptyList()).forGetter(UHCConfig::modules))
			.apply(instance, UHCConfig::new));
	private final PlayerConfig playerConfig;
	private final int teamSize;
	private final UHCMapConfig mapConfig;
	private final UHCChapterConfig timeConfig;
	private final List<Module> modules;

	private final Map<ModulePieceType<?>, List<ModulePiece>> modulePieces;

	public UHCConfig(PlayerConfig players, int teamSize, UHCMapConfig mapConfig, UHCChapterConfig timeConfig, List<Module> modules) {
		this.playerConfig = players;
		this.teamSize = teamSize;
		this.mapConfig = mapConfig;
		this.timeConfig = timeConfig;
		this.modules = modules;

		this.modulePieces = new HashMap<>();
		for(Module module : modules) {
			for(ModulePiece modulePiece : module.pieces()) {
				if(!modulePieces.containsKey(modulePiece.getType())) modulePieces.put(modulePiece.getType(), new ArrayList<>());
				modulePieces.get(modulePiece.getType()).add(modulePiece);
			}
		}
	}

	public PlayerConfig playerConfig() {
		return playerConfig;
	}

	public int teamSize() {
		return teamSize;
	}

	public UHCMapConfig mapConfig() {
		return mapConfig;
	}

	public UHCChapterConfig timeConfig() {
		return timeConfig;
	}

	public List<Module> modules() {
		return modules;
	}

	public <V extends ModulePiece> List<V> getModulesPieces(ModulePieceType<V> pieceType) {
		if(!modulePieces.containsKey(pieceType)) return Collections.emptyList();
		return (List<V>) modulePieces.get(pieceType);
	}
}
