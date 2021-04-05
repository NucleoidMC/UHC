package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.fabricmc.fabric.mixin.biome.VanillaLayeredBiomeSourceAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class UHCMap {
	private final UHCMapConfig config;

	public UHCMap(UHCMapConfig config) {
		this.config = config;
	}

	public ChunkGenerator getChunkGenerator(MinecraftServer server) {
		long seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLikeBiomeSource(seed, server.getRegistryManager().get(Registry.BIOME_KEY), createBiomeList());
		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(this.config.getChunkSettings());
		return new NoiseChunkGenerator(biomeSource, seed, () -> chunkGeneratorSettings);
	}

	public List<RegistryKey<Biome>> createBiomeList() {
		List<RegistryKey<Biome>> list = new ArrayList<>(VanillaLayeredBiomeSourceAccessor.getBIOMES());
		removeOceans(list);
		return list;
	}

	public void removeOceans(List<RegistryKey<Biome>> list) {
		list.remove(BiomeKeys.OCEAN);
		list.remove(BiomeKeys.COLD_OCEAN);
		list.remove(BiomeKeys.LUKEWARM_OCEAN);
		list.remove(BiomeKeys.WARM_OCEAN);
		list.remove(BiomeKeys.FROZEN_OCEAN);
		list.remove(BiomeKeys.DEEP_FROZEN_OCEAN);
		list.remove(BiomeKeys.DEEP_LUKEWARM_OCEAN);
		list.remove(BiomeKeys.DEEP_WARM_OCEAN);
		list.remove(BiomeKeys.DEEP_COLD_OCEAN);
		list.remove(BiomeKeys.DEEP_OCEAN);
	}
}
