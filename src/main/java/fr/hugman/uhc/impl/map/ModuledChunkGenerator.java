package fr.hugman.uhc.impl.map;

import com.mojang.datafixers.util.Pair;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.impl.game.ModuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
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
    private final NoiseGeneratorSettings settings;

    public ModuledChunkGenerator(BiomeSource biomeSource, List<PlacedFeature> placedFeatures, long seed, ChunkGenerator subGenerator, NoiseGeneratorSettings settings) {
        super(biomeSource);
        this.placedFeatures = placedFeatures;
        this.seed = seed;
        this.subGenerator = subGenerator;
        this.settings = settings;
    }

    public static ModuledChunkGenerator of(UHCGameConfig config, long seed, HolderLookup.Provider registries) {
        var dimension = registries.lookupOrThrow(Registries.LEVEL_STEM).getOrThrow(config.uhcConfig().value().mapConfig().dimension()).value();
        BiomeSource biomeSource = dimension.generator().getBiomeSource();
        ChunkGenerator subGenerator = dimension.generator();
        NoiseGeneratorSettings settings = null;
        if (subGenerator instanceof NoiseBasedChunkGenerator generator) {
            settings = generator.generatorSettings().value();
        }

        List<PlacedFeature> placedFeatures = ModuleManager.streamModifiers(config.uhcConfig().value().modules().stream(), ModifierType.PLACED_FEATURES)
                .flatMap(modifier -> modifier.features().stream())
                .map(Holder::value)
                .collect(Collectors.toList());

        return new ModuledChunkGenerator(biomeSource, placedFeatures, seed, subGenerator, settings);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
        var chunkPos = chunk.getPos();
        if (SharedConstants.debugVoidTerrain(chunkPos)) {
            return;
        }
        var blockPos = SectionPos.of(chunk.getPos(), world.getMinSectionY()).origin();

        WorldgenRandom chunkRandom = new WorldgenRandom(new XoroshiroRandomSource(this.seed));
        long popSeed = chunkRandom.setDecorationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());

        int i = 0;
        var placedFeatureRegistry = world.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
        for (var placedFeature : this.placedFeatures) {
            var name = placedFeatureRegistry.getResourceKey(placedFeature).map(Object::toString);
            chunkRandom.setFeatureSeed(popSeed, i++, 0);
            world.setCurrentlyGenerating(() -> name.orElse("Custom UHC placed feature"));
            placedFeature.placeWithBiomeCheck(world, this, chunkRandom, blockPos);
        }
        world.setCurrentlyGenerating(null);
        this.subGenerator.applyBiomeDecoration(world, chunk, structureAccessor);
    }

    /*=================*/
    /*  SUB OVERRIDES  */
    /*=================*/

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(RandomState noiseConfig, Blender blender, StructureManager structureAccessor, ChunkAccess chunk) {
        return this.subGenerator.createBiomes(noiseConfig, blender, structureAccessor, chunk);
    }

    @Override
    public BiomeSource getBiomeSource() {
        return this.subGenerator.getBiomeSource();
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {
        this.subGenerator.buildSurface(region, structures, noiseConfig, chunk);
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        return this.subGenerator.fillFromNoise(blender, noiseConfig, structureAccessor, chunk);
    }

    @Override
    public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager world, StructureManager structureAccessor, ChunkAccess chunk) {
        this.subGenerator.applyCarvers(chunkRegion, seed, noiseConfig, world, structureAccessor, chunk);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        this.subGenerator.spawnOriginalMobs(region);
    }

    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel world, HolderSet<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return this.subGenerator.findNearestMapStructure(world, structures, center, radius, skipReferencedStructures);
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> structureSetRegistry, RandomState noiseConfig, long seed) {
        return this.subGenerator.createState(structureSetRegistry, noiseConfig, seed);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return this.subGenerator.getBaseHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getFirstOccupiedHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return this.subGenerator.getFirstOccupiedHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getFirstFreeHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return this.subGenerator.getFirstFreeHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getGenDepth() {
        return this.subGenerator.getGenDepth();
    }

    @Override
    public int getSeaLevel() {
        return this.subGenerator.getSeaLevel();
    }

    @Override
    public int getMinY() {
        return this.subGenerator.getMinY();
    }

    @Override
    public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos) {
        this.subGenerator.addDebugScreenInfo(text, noiseConfig, pos);
    }

    @Override
    public WeightedList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureManager accessor, MobCategory group, BlockPos pos) {
        return this.subGenerator.getMobsAt(biome, accessor, group, pos);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig) {
        return this.subGenerator.getBaseColumn(x, z, world, noiseConfig);
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor world) {
        return this.subGenerator.getSpawnHeight(world);
    }

    @Override
    public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> biomeEntry) {
        return this.subGenerator.getBiomeGenerationSettings(biomeEntry);
    }

    private long getSeed() {
        return seed;
    }

    @Override
    public @Nullable NoiseGeneratorSettings getSettings() {
        return this.settings;
    }
}
