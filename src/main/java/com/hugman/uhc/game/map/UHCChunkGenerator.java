package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.OreModulePiece;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
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
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UHCChunkGenerator extends GameChunkGenerator {
	private final UHCConfig config;
	private final long seed;
	private final ChunkGenerator subGenerator;

	public UHCChunkGenerator(MinecraftServer server, UHCConfig config) {
		super(server);
		this.config = config;

		this.seed = server.getOverworld().getRandom().nextLong();
		BiomeSource biomeSource = new VanillaLayeredBiomeSource(this.seed, false, false, server.getRegistryManager().get(Registry.BIOME_KEY));

		ChunkGeneratorSettings chunkGeneratorSettings = BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.get(this.config.getMapConfig().chunkSettings());
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
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		return this.subGenerator.populateNoise(executor, accessor, chunk);
	}

	@Override
	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		this.subGenerator.carve(seed, access, chunk, carver);
	}

	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structures) {
		Random random = new Random();
		for(OreModulePiece piece : this.config.oreModulePieces) {
			int x = random.nextInt(16) + (region.getCenterPos().getRegionX() * 16);
			int y = random.nextInt(64);
			int z = random.nextInt(16) + (region.getCenterPos().getRegionZ() * 16);
			piece.generate(region, this, random, new BlockPos(x, y, z));
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
