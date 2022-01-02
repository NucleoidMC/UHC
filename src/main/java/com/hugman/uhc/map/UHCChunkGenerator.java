package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.ModulePieceType;
import com.hugman.uhc.module.piece.PlacedFeaturesModulePiece;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UHCChunkGenerator extends GameChunkGenerator {
	private final UHCConfig config;
	private final long seed;
	private final ChunkGenerator subGenerator;

	public UHCChunkGenerator(MinecraftServer server, UHCConfig config, long seed) {
		super(server);
		this.config = config;
		this.seed = seed;
		this.subGenerator = GeneratorOptions.createOverworldGenerator(server.getRegistryManager(), seed);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(Registry<Biome> biomeRegistry, Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return this.subGenerator.populateBiomes(biomeRegistry, executor, blender, structureAccessor, chunk);
	}

	@Override
	public BiomeSource getBiomeSource() {
		return this.subGenerator.getBiomeSource();
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
		this.subGenerator.buildSurface(region, structures, chunk);
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return this.subGenerator.populateNoise(executor, blender, structureAccessor, chunk);
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
		this.subGenerator.carve(chunkRegion, seed, biomeAccess, structureAccessor, chunk, generationStep);
	}

	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		BlockPos blockPos = new BlockPos(i, chunk.getBottomY(), j);
		ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(this.seed));
		chunkRandom.setPopulationSeed(world.getSeed(), i, j);
		for(PlacedFeaturesModulePiece piece : this.config.getModulesPieces(ModulePieceType.PLACED_FEATURES)) {
			piece.getValues(world).forEach(v -> v.generate(world, this, chunkRandom, blockPos));
		}
		this.subGenerator.generateFeatures(world, chunk, structureAccessor);
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
