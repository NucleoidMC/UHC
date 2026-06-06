package fr.hugman.uhc.impl.map;

import fr.hugman.uhc.api.config.UHCGameConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;

public class UHCMap {
    private final UHCGameConfig config;
    private final ChunkGenerator chunkGenerator;
    private final HolderLookup.Provider registries;
    private final long seed;

    public UHCMap(UHCGameConfig config, ChunkGenerator chunkGenerator, HolderLookup.Provider registries, long seed) {
        this.config = config;
        this.chunkGenerator = chunkGenerator;
        this.registries = registries;
        this.seed = seed;
    }

    public static UHCMap of(UHCGameConfig config, HolderLookup.Provider registries) {
        long seed = WorldOptions.randomSeed();
        return new UHCMap(config, ModuledChunkGenerator.of(config, seed, registries), registries, seed);
    }

    public RuntimeLevelConfig createRuntimeLevelConfig() {
        var dimension = registries.lookupOrThrow(Registries.LEVEL_STEM).getOrThrow(config.uhcConfig().value().mapConfig().dimension()).value();

        return new RuntimeLevelConfig()
                .setSeed(this.seed)
                .setGenerator(this.chunkGenerator)
                .setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false)
                .setGameRule(GameRules.SPAWN_MOBS, true)
                .setGameRule(GameRules.ADVANCE_TIME, true)
                .setDimensionType(dimension.type());
    }
}
