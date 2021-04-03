package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

public class UHCChunkGenerator extends GameChunkGenerator {
	private static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();

	private final UHCMapConfig mapConfig;
	private final long seed;
	private final ChunkGenerator chunkGenerator;

	public UHCChunkGenerator(MinecraftServer server, UHCMapConfig mapConfig) {
		super(server);
		this.mapConfig = mapConfig;

		this.seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(this.seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));

		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(mapConfig.chunkSettings);
		this.chunkGenerator = new NoiseChunkGenerator(biomeSource, this.seed, () -> chunkGeneratorSettings);
	}

	private boolean isChunkPosWithinArea(ChunkPos chunkPos) {
		return chunkPos.x >= 0 && chunkPos.z >= 0 && chunkPos.x < this.mapConfig.borderConfig.startSize && chunkPos.z < this.mapConfig.borderConfig.startSize;
	}

	private boolean isChunkWithinArea(Chunk chunk) {
		return this.isChunkPosWithinArea(chunk.getPos());
	}

	@Override
	public void populateBiomes(Registry<Biome> registry, Chunk chunk) {
		if (this.isChunkWithinArea(chunk)) {
			this.chunkGenerator.populateBiomes(registry, chunk);
		} else {
			super.populateBiomes(registry, chunk);
		}
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structures, Chunk chunk) {
		if (this.isChunkWithinArea(chunk)) {
			this.chunkGenerator.populateNoise(world, structures, chunk);
		}
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		if (this.isChunkWithinArea(chunk)) {
			this.chunkGenerator.buildSurface(region, chunk);
		}
	}

	@Override
	public BiomeSource getBiomeSource() {
		return this.chunkGenerator.getBiomeSource();
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		int chunkX = region.getCenterChunkX();
		int chunkZ = region.getCenterChunkZ();

		ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
		if (!this.isChunkPosWithinArea(chunkPos)) return;

		BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
		Biome biome = this.chunkGenerator.getBiomeSource().getBiomeForNoiseGen((chunkX << 2) + 2, 2, (chunkZ << 2) + 2);

		ChunkRandom chunkRandom = new ChunkRandom();
		long populationSeed = chunkRandom.setPopulationSeed(this.seed, pos.getX(), pos.getZ());

		biome.generateFeatureStep(structures, this.chunkGenerator, region, populationSeed, chunkRandom, pos);

		BlockPos.Mutable mutablePos = new BlockPos.Mutable();
		Chunk chunk = region.getChunk(chunkPos.getStartPos());

		// Top
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				mutablePos.set(x + pos.getX(), 255, z + pos.getZ());
				chunk.setBlockState(mutablePos, BARRIER, false);
			}
		}

		// North
		if (chunkZ == 0) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					mutablePos.set(x + pos.getX(), y, pos.getZ());
					chunk.setBlockState(mutablePos, BARRIER, false);
				}
			}
		}

		// East
		if (chunkX == this.mapConfig.borderConfig.startSize - 1) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					mutablePos.set(pos.getX() + 15, y, z + pos.getZ());
					chunk.setBlockState(mutablePos, BARRIER, false);
				}
			}
		}

		// South
		if (chunkZ == this.mapConfig.borderConfig.startSize - 1) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					mutablePos.set(x + pos.getX(), y, pos.getZ() + 15);
					chunk.setBlockState(mutablePos, BARRIER, false);
				}
			}
		}

		// West
		if (chunkX == 0) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					mutablePos.set(pos.getX(), y, z + pos.getZ());
					chunk.setBlockState(mutablePos, BARRIER, false);
				}
			}
		}
	}

	@Override
	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		if (this.isChunkWithinArea(chunk)) {
			this.chunkGenerator.carve(this.seed, access, chunk, carver);
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		if (this.isChunkPosWithinArea(new ChunkPos(x >> 4, z >> 4))) {
			return this.chunkGenerator.getHeight(x, z, heightmapType);
		}
		return super.getHeight(x, z, heightmapType);
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		if (this.isChunkPosWithinArea(new ChunkPos(x >> 4, z >> 4))) {
			return this.chunkGenerator.getColumnSample(x, z);
		}
		return super.getColumnSample(x, z);
	}
}
