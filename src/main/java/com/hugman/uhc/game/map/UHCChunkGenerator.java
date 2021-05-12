package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public class UHCChunkGenerator extends GameChunkGenerator {
	private final UHCMapConfig mapConfig;
	private final long seed;
	private final ChunkGenerator subGenerator;

	public UHCChunkGenerator(MinecraftServer server, UHCMapConfig mapConfig) {
		super(server);
		this.mapConfig = mapConfig;

		this.seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(this.seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));

		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(this.mapConfig.getChunkSettingsId());
		this.subGenerator = new NoiseChunkGenerator(biomeSource, this.seed, () -> chunkGeneratorSettings);
	}

	@Override
	public void populateBiomes(Registry<Biome> biomeRegistry, Chunk chunk) {
		this.subGenerator.populateBiomes(biomeRegistry, chunk);
	}

	@Override
	public BiomeSource getBiomeSource() {
		return this.subGenerator.getBiomeSource();
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		this.subGenerator.buildSurface(region, chunk);
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structures, Chunk chunk) {
		this.subGenerator.populateNoise(world, structures, chunk);
	}

	@Override
	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		this.subGenerator.carve(seed, access, chunk, carver);
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		this.subGenerator.generateFeatures(region, structures);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		this.subGenerator.populateEntities(region);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return this.subGenerator.getHeight(x, z, heightmapType);
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return this.subGenerator.getColumnSample(x, z);
	}
}
