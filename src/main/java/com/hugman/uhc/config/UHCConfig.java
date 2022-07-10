package com.hugman.uhc.config;

import com.hugman.uhc.UHC;
import com.hugman.uhc.UHCRegistries;
import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieceType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.RegistryWorldView;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.*;

public record UHCConfig(PlayerConfig players, int teamSize, UHCMapConfig mapConfig, UHCChapterConfig chapterConfig, List<Identifier> moduleIds) {
	public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(UHCConfig::players),
			Codec.INT.fieldOf("team_size").forGetter(UHCConfig::teamSize),
			UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
			UHCChapterConfig.CODEC.fieldOf("chapters").forGetter(UHCConfig::chapterConfig),
			Identifier.CODEC.listOf().fieldOf("modules").orElse(List.of()).forGetter(UHCConfig::moduleIds)
			// NOTE: we cannot use Module.REGISTRY_LIST_CODEC as Plasmid
			// loads the game configs before all the other dynamic registries
			// we need to work around this and use raw identifiers there
	).apply(instance, UHCConfig::new));

	public List<Module> getModules(RegistryWorldView world) {
		return getModules(world.getRegistryManager());
	}

	public List<Module> getModules(DynamicRegistryManager registryManager) {
		List<Module> modules = new ArrayList<>();
		if(!this.moduleIds.isEmpty()) {
			Registry<Module> registry = registryManager.get(UHCRegistries.MODULE.getKey());
			for(Identifier moduleId : this.moduleIds) {
				Module module = registry.get(moduleId);
				if(module == null) {
					UHC.LOGGER.error("Module {} not found", moduleId);
					continue;
				}
				modules.add(module);
			}
		}
		return modules;
	}

	public List<ModulePiece> getModulesPieces(RegistryWorldView world) {
		return getModulesPieces(world.getRegistryManager());
	}

	public List<ModulePiece> getModulesPieces(DynamicRegistryManager registryManager) {
		List<ModulePiece> modulePieces = new ArrayList<>();
		if(!this.moduleIds.isEmpty()) {
			Registry<Module> registry = registryManager.get(UHCRegistries.MODULE.getKey());
			for(Identifier moduleId : this.moduleIds) {
				Module module = registry.get(moduleId);
				if(module == null) {
					continue;
				}
				modulePieces.addAll(module.pieces());
			}
		}
		return modulePieces;
	}

	public <V extends ModulePiece> List<V> getModulesPieces(RegistryWorldView world, ModulePieceType<V> type) {
		return getModulesPieces(world.getRegistryManager(), type);
	}

	public <V extends ModulePiece> List<V> getModulesPieces(DynamicRegistryManager registryManager, ModulePieceType<V> type) {
		return (List<V>) getModulesPieces(registryManager).stream().filter(piece -> piece.getType().equals(type)).toList();
	}
}
