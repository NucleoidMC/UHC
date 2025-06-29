package fr.hugman.uhc.impl.map;

import fr.hugman.uhc.api.config.UHCGameConfig;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class UHCMap {
    private final UHCGameConfig config;
    private final ChunkGenerator chunkGenerator;
    private final RegistryWrapper.WrapperLookup registries;
    private final long seed;

    public UHCMap(UHCGameConfig config, ChunkGenerator chunkGenerator, RegistryWrapper.WrapperLookup registries, long seed) {
        this.config = config;
        this.chunkGenerator = chunkGenerator;
        this.registries = registries;
        this.seed = seed;
    }

    public static UHCMap of(UHCGameConfig config, RegistryWrapper.WrapperLookup registries) {
        long seed = GeneratorOptions.getRandomSeed();
        return new UHCMap(config, ModuledChunkGenerator.of(config, seed, registries), registries, seed);
    }

    public RuntimeWorldConfig createRuntimeWorldConfig() {
        var dimension = registries.getOrThrow(RegistryKeys.DIMENSION).getOrThrow(config.uhcConfig().value().mapConfig().dimension()).value();

        return new RuntimeWorldConfig()
                .setSeed(this.seed)
                .setGenerator(this.chunkGenerator)
                .setGameRule(GameRules.NATURAL_REGENERATION, false)
                .setGameRule(GameRules.DO_MOB_SPAWNING, true)
                .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
                .setDimensionType(dimension.dimensionTypeEntry());
    }
}
