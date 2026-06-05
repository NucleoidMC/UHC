package fr.hugman.uhc.impl.map;

import fr.hugman.uhc.api.config.UHCGameConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldOptions;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

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

    public RuntimeWorldConfig createRuntimeWorldConfig() {
        var dimension = registries.lookupOrThrow(Registries.LEVEL_STEM).getOrThrow(config.uhcConfig().value().mapConfig().dimension()).value();

        return new RuntimeWorldConfig()
                .setSeed(this.seed)
                .setGenerator(this.chunkGenerator)
                .setGameRule(GameRules.RULE_NATURAL_REGENERATION, false)
                .setGameRule(GameRules.RULE_DOMOBSPAWNING, true)
                .setGameRule(GameRules.RULE_DAYLIGHT, true)
                .setDimensionType(dimension.type());
    }
}
