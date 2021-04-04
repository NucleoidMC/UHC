package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class UHCMap {
	public ChunkGenerator getChunkGenerator(MinecraftServer server, UHCMapConfig mapConfig) {
		long seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));
		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(mapConfig.getChunkSettings());
		return new NoiseChunkGenerator(biomeSource, seed, () -> chunkGeneratorSettings);
	}
}
