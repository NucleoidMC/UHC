package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class UHCMap {
	private final UHCMapConfig config;
	private final BlockBox box;

	public UHCMap(UHCMapConfig mapConfig) {
		this.config = mapConfig;
		this.box = new BlockBox(1, 1, 1, mapConfig.borderConfig.startSize * 16 - 2, 254, mapConfig.borderConfig.startSize * 16 - 2);
	}

	public BlockBox getBox() {
		return box;
	}

	public ChunkGenerator getChunkGenerator(MinecraftServer server) {
		long seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));
		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(this.config.chunkSettings);
		return new NoiseChunkGenerator(biomeSource, seed, () -> chunkGeneratorSettings);
	}
}
