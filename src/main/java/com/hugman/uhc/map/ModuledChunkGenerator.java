package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCGameConfig;
import com.hugman.uhc.game.ModuleManager;
import com.hugman.uhc.modifier.ModifierType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.util.ChunkGeneratorSettingsProvider;
import xyz.nucleoid.plasmid.api.game.world.generator.GameChunkGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModuledChunkGenerator extends GameChunkGenerator implements ChunkGeneratorSettingsProvider {
    private final List<PlacedFeature> placedFeatures;
    private final long seed;
    private final ChunkGenerator subGenerator;
    private final ChunkGeneratorSettings settings;

    public ModuledChunkGenerator(BiomeSource biomeSource, List<PlacedFeature> placedFeatures, long seed, ChunkGenerator subGenerator, ChunkGeneratorSettings settings) {
        super(biomeSource);
        this.placedFeatures = placedFeatures;
        this.seed = seed;
        this.subGenerator = subGenerator;
        this.settings = settings;
    }

    public static ModuledChunkGenerator of(UHCGameConfig config, long seed) {
        BiomeSource biomeSource = config.uhcConfig().value().mapConfig().dimension().chunkGenerator().getBiomeSource();
        ChunkGenerator subGenerator = config.uhcConfig().value().mapConfig().dimension().chunkGenerator();
        ChunkGeneratorSettings settings = null;
        if (subGenerator instanceof NoiseChunkGenerator generator) {
            settings = generator.getSettings().value();
        }

        List<PlacedFeature> placedFeatures = ModuleManager.streamModifiers(config.uhcConfig().value().modules().stream(), ModifierType.PLACED_FEATURES)
                .flatMap(modifier -> modifier.features().stream())
                .map(RegistryEntry::value)
                .collect(Collectors.toList());

        return new ModuledChunkGenerator(biomeSource, placedFeatures, seed, subGenerator, settings);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        var chunkPos = chunk.getPos();
        if (SharedConstants.isOutsideGenerationArea(chunkPos)) {
            return;
        }
        var blockPos = ChunkSectionPos.from(chunk.getPos(), world.getBottomSectionCoord()).getMinPos();

        ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(this.seed));
        long popSeed = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());

        int i = 0;
        for (var placedFeature : this.placedFeatures) {
            chunkRandom.setDecoratorSeed(popSeed, i++, 0);
            world.setCurrentlyGeneratingStructureName(() -> "custom UHC feature");
            placedFeature.generate(world, this, chunkRandom, blockPos);
        }
        world.setCurrentlyGeneratingStructureName(null);
        this.subGenerator.generateFeatures(world, chunk, structureAccessor);
    }

    /*=================*/
    /*  SUB OVERRIDES  */
    /*=================*/

    @Override
    public CompletableFuture<Chunk> populateBiomes(NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return this.subGenerator.populateBiomes(noiseConfig, blender, structureAccessor, chunk);
    }

    @Override
    public BiomeSource getBiomeSource() {
        return this.subGenerator.getBiomeSource();
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        this.subGenerator.buildSurface(region, structures, noiseConfig, chunk);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return this.subGenerator.populateNoise(blender, noiseConfig, structureAccessor, chunk);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess world, StructureAccessor structureAccessor, Chunk chunk) {
        this.subGenerator.carve(chunkRegion, seed, noiseConfig, world, structureAccessor, chunk);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        this.subGenerator.populateEntities(region);
    }

    @Nullable
    @Override
    public Pair<BlockPos, RegistryEntry<Structure>> locateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return this.subGenerator.locateStructure(world, structures, center, radius, skipReferencedStructures);
    }

    @Override
    public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper<StructureSet> structureSetRegistry, NoiseConfig noiseConfig, long seed) {
        return this.subGenerator.createStructurePlacementCalculator(structureSetRegistry, noiseConfig, seed);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.subGenerator.getHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.subGenerator.getHeightInGround(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.subGenerator.getHeightOnGround(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getWorldHeight() {
        return this.subGenerator.getWorldHeight();
    }

    @Override
    public int getSeaLevel() {
        return this.subGenerator.getSeaLevel();
    }

    @Override
    public int getMinimumY() {
        return this.subGenerator.getMinimumY();
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        this.subGenerator.appendDebugHudText(text, noiseConfig, pos);
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        return this.subGenerator.getEntitySpawnList(biome, accessor, group, pos);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.subGenerator.getColumnSample(x, z, world, noiseConfig);
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return this.subGenerator.getSpawnHeight(world);
    }

    @Override
    public GenerationSettings getGenerationSettings(RegistryEntry<Biome> biomeEntry) {
        return this.subGenerator.getGenerationSettings(biomeEntry);
    }

    private long getSeed() {
        return seed;
    }

    @Override
    public @Nullable ChunkGeneratorSettings getSettings() {
        return this.settings;
    }
}
