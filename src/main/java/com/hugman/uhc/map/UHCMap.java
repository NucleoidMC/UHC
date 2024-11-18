package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCGameConfig;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class UHCMap {
    private final UHCGameConfig config;
    private final ChunkGenerator chunkGenerator;
    private final long seed;

    public UHCMap(UHCGameConfig config, ChunkGenerator chunkGenerator, long seed) {
        this.config = config;
        this.chunkGenerator = chunkGenerator;
        this.seed = seed;
    }

    public static UHCMap of(UHCGameConfig config) {
        long seed = GeneratorOptions.getRandomSeed();
        return new UHCMap(config, ModuledChunkGenerator.of(config, seed), seed);
    }

    public RuntimeWorldConfig createRuntimeWorldConfig() {
        return new RuntimeWorldConfig()
                .setSeed(this.seed)
                .setGenerator(this.chunkGenerator)
                .setGameRule(GameRules.NATURAL_REGENERATION, false)
                .setGameRule(GameRules.DO_MOB_SPAWNING, true)
                .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
                .setDimensionType(this.config.uhcConfig().value().mapConfig().dimension().dimensionTypeEntry());
    }
}
