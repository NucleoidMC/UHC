package fr.hugman.uhc.impl.map;

import fr.hugman.uhc.api.config.UHCConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;

public class UHCMap {
    private final Holder<DimensionType> dimensionType;
    private final ChunkGenerator chunkGenerator;
    private final long seed;

    public UHCMap(Holder<DimensionType> dimensionType, ChunkGenerator chunkGenerator, long seed) {
        this.dimensionType = dimensionType;
        this.chunkGenerator = chunkGenerator;
        this.seed = seed;
    }

    public static UHCMap of(UHCConfig config, HolderLookup.Provider registries) {
        long seed = WorldOptions.randomSeed();
        return new UHCMap(config.mapConfig().dimensionType(), ModuledChunkGenerator.of(
                config.mapConfig().chunkGenerator(),
                config.modules(),
                config.mapConfig().excludedBiomes(),
                seed,
                registries
        ), seed);
    }

    public RuntimeLevelConfig createRuntimeLevelConfig() {
        return new RuntimeLevelConfig()
                .setSeed(this.seed)
                .setGenerator(this.chunkGenerator)
                .setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false)
                .setGameRule(GameRules.SPAWN_MOBS, true)
                .setGameRule(GameRules.ADVANCE_TIME, true)
                .setDimensionType(dimensionType);
    }
}
