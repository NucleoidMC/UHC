package com.hugman.uhc.config;

import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.Modules;
import com.hugman.uhc.module.piece.BlockLootModulePiece;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.EntityLootModulePiece;
import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieces;
import com.hugman.uhc.module.piece.OreModulePiece;
import com.hugman.uhc.module.piece.PermanentEffectModulePiece;
import com.hugman.uhc.module.piece.PlayerAttributeModulePiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UHCConfig {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(UHCConfig::playerConfig),
			Codec.INT.fieldOf("team_size").forGetter(UHCConfig::teamSize),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
			UHCChapterConfig.CODEC.fieldOf("chapters").forGetter(UHCConfig::timeConfig),
			Identifier.CODEC.listOf().optionalFieldOf("modules", Collections.emptyList()).forGetter(UHCConfig::modulesIds)
	).apply(instance, UHCConfig::new));
	public final List<BlockLootModulePiece> blockLootModulePieces;
	public final List<EntityLootModulePiece> entityLootModulePieces;
	public final List<BucketBreakModulePiece> bucketBreakModulePieces;
	public final List<PlayerAttributeModulePiece> playerAttributeModulePieces;
	public final List<PermanentEffectModulePiece> permanentEffectModulePieces;
	public final List<OreModulePiece> oreModulePieces;
	private final PlayerConfig playerConfig;
	private final int teamSize;
	private final UHCMapConfig mapConfig;
	private final UHCChapterConfig timeConfig;
	private final List<Identifier> modulesIds;
	private final List<Module> modules;
	private final List<ModulePiece> modulesPieces;

	public UHCConfig(PlayerConfig players, int teamSize, UHCMapConfig mapConfig, UHCChapterConfig timeConfig, List<Identifier> modulesIds) {
		this.playerConfig = players;
		this.teamSize = teamSize;
		this.mapConfig = mapConfig;
		this.timeConfig = timeConfig;
		this.modulesIds = modulesIds;

		this.modules = modulesIds.stream().map(Modules::get).collect(Collectors.toList());
		this.modulesPieces = new ArrayList<>();
		this.modules.forEach(module -> modulesPieces.addAll(module.pieces()));

		this.blockLootModulePieces = getAllModulesPieces(ModulePieces.BLOCK_LOOT);
		this.entityLootModulePieces = getAllModulesPieces(ModulePieces.ENTITY_LOOT);
		this.bucketBreakModulePieces = getAllModulesPieces(ModulePieces.BUCKET_BREAK);
		this.playerAttributeModulePieces = getAllModulesPieces(ModulePieces.PLAYER_ATTRIBUTES);
		this.permanentEffectModulePieces = getAllModulesPieces(ModulePieces.PERMANENT_EFFECTS);
		this.oreModulePieces = getAllModulesPieces(ModulePieces.ORE);
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

	private List<Identifier> modulesIds() {
		return modulesIds;
	}

	public List<Module> modules() {
		return modules;
	}

	private <V extends ModulePiece> List<V> getAllModulesPieces(Identifier id) {
		return modulesPieces.stream().filter(item -> ModulePieces.getId(item).equals(id))
				.map(piece -> (V) piece)
				.collect(Collectors.toList());
	}
}
