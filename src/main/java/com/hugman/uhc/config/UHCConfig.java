package com.hugman.uhc.config;

import com.hugman.uhc.UHC;
import com.hugman.uhc.UHCRegistries;
import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.ArrayList;
import java.util.List;

public record UHCConfig(PlayerConfig players, int teamSize, UHCMapConfig mapConfig, UHCChapterConfig chapterConfig,
						List<Identifier> moduleIds) {
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

	public List<Module> getModules() {
		List<Module> modules = new ArrayList<>();
		if (!this.moduleIds.isEmpty()) {
			for (Identifier moduleId : this.moduleIds) {
				Module module = UHCRegistries.MODULE.get(moduleId);
				if (module == null) {
					UHC.LOGGER.error("Module {} not found", moduleId);
					continue;
				}
				modules.add(module);
			}
		}
		return modules;
	}

	public List<Modifier> getModulesPieces() {
		List<Modifier> modifiers = new ArrayList<>();
		if (!this.moduleIds.isEmpty()) {
			for (Identifier moduleId : this.moduleIds) {
				Module module = UHCRegistries.MODULE.get(moduleId);
				if (module == null) {
					continue;
				}
				modifiers.addAll(module.pieces());
			}
		}
		return modifiers;
	}

	public <V extends Modifier> List<V> getModulesPieces(ModifierType<V> type) {
		return (List<V>) getModulesPieces().stream().filter(piece -> piece.getType().equals(type)).toList();
	}
}
