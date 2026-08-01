package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.util.DoubleRange;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

public record UHCMapConfig(
        Holder<DimensionType> dimensionType,
        ChunkGenerator chunkGenerator,
        Optional<HolderSet<Biome>> excludedBiomes,
        DoubleRange startSize,
        DoubleRange endSize,
        double shrinkingSpeed,
        int spawnOffset
) {
    private static final DoubleRange DEFAULT_END_SIZE = new DoubleRange(15, 40);
    private static final double DEFAULT_SHRINKING_SPEED = 1.0D;
    private static final int DEFAULT_SPAWN_OFFSET = 40;

    public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DimensionType.CODEC.fieldOf("type").forGetter(UHCMapConfig::dimensionType),
            ChunkGenerator.CODEC.fieldOf("generator").forGetter(UHCMapConfig::chunkGenerator),
            RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("excluded_biomes").forGetter(mapConfig -> mapConfig.excludedBiomes),
            DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::startSize),
            DoubleRange.CODEC.optionalFieldOf("end_size", DEFAULT_END_SIZE).forGetter(UHCMapConfig::endSize),
            Codec.DOUBLE.optionalFieldOf("shrinking_speed", DEFAULT_SHRINKING_SPEED).forGetter(UHCMapConfig::shrinkingSpeed),
            Codec.INT.optionalFieldOf("spawn_offset", DEFAULT_SPAWN_OFFSET).forGetter(UHCMapConfig::spawnOffset)
    ).apply(instance, UHCMapConfig::new));

    public static UHCMapConfig of(Holder<DimensionType> dimensionType, ChunkGenerator chunkGenerator, DoubleRange startSize, double shrinkingSpeed) {
        return new UHCMapConfig(dimensionType, chunkGenerator, Optional.empty(), startSize, DEFAULT_END_SIZE, shrinkingSpeed, DEFAULT_SPAWN_OFFSET);
    }

    public static UHCMapConfig of(Holder<DimensionType> dimensionType, ChunkGenerator chunkGenerator, HolderSet<Biome> excludedBiomes, DoubleRange startSize, double shrinkingSpeed) {
        return new UHCMapConfig(dimensionType, chunkGenerator, Optional.of(excludedBiomes), startSize, DEFAULT_END_SIZE, shrinkingSpeed, DEFAULT_SPAWN_OFFSET);
    }

    public UHCMapConfig withStartSize(DoubleRange startSize) {
        return new UHCMapConfig(dimensionType, chunkGenerator, excludedBiomes, startSize, endSize, shrinkingSpeed, spawnOffset);
    }

    public UHCMapConfig withEndSize(DoubleRange endSize) {
        return new UHCMapConfig(dimensionType, chunkGenerator, excludedBiomes, startSize, endSize, shrinkingSpeed, spawnOffset);
    }

    public UHCMapConfig withShrinkingSpeed(double shrinkingSpeed) {
        return new UHCMapConfig(dimensionType, chunkGenerator, excludedBiomes, startSize, endSize, shrinkingSpeed, spawnOffset);
    }

    public UHCMapConfig withSpawnOffset(int spawnOffset) {
        return new UHCMapConfig(dimensionType, chunkGenerator, excludedBiomes, startSize, endSize, shrinkingSpeed, spawnOffset);
    }

    @Override
    public UHCMapConfig clone() {
        return new UHCMapConfig(dimensionType, chunkGenerator, excludedBiomes, startSize, endSize, shrinkingSpeed, spawnOffset);
    }
}
