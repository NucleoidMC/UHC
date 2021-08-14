package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.OreModulePiece;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UHCChunkGenerator extends GameChunkGenerator {
	private final UHCConfig config;
	private final ChunkGenerator subGenerator;

	public UHCChunkGenerator(MinecraftServer server, UHCConfig config) {
		super(server);
		this.config = config;

		long seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));

		ChunkGeneratorSettings chunkGeneratorSettings = this.config.mapConfig().chunkSettings();
		this.subGenerator = new NoiseChunkGenerator(biomeSource, seed, () -> chunkGeneratorSettings);
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
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		return this.subGenerator.populateNoise(executor, accessor, chunk);
	}

	@Override
	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		this.subGenerator.carve(seed, access, chunk, carver);
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		ChunkPos chunkPos = region.getCenterPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		BlockPos blockPos = new BlockPos(i, region.getBottomY(), j);
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setPopulationSeed(region.getSeed(), i, j);
		for(OreModulePiece piece : this.config.oreModulePieces) {
			piece.generate(region, this, chunkRandom, blockPos);
		}

		this.subGenerator.generateFeatures(region, structures);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		this.subGenerator.populateEntities(region);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		return this.subGenerator.getHeight(x, z, heightmap, world);
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		return this.subGenerator.getColumnSample(x, z, world);
	}
}
