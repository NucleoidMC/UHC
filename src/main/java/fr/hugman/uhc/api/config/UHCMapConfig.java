package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.util.DoubleRange;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public record UHCMapConfig(
        ResourceKey<LevelStem> dimension,
        DoubleRange startSize,
        DoubleRange endSize,
        double shrinkingSpeed,
        int spawnOffset
) {
    private static final DoubleRange DEFAULT_END_SIZE = new DoubleRange(15, 40);
    private static final double DEFAULT_SHRINKING_SPEED = 1.0D;
    private static final int DEFAULT_SPAWN_OFFSET = 40;

    public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.LEVEL_STEM).fieldOf("dimension").forGetter(UHCMapConfig::dimension),
            DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::startSize),
            DoubleRange.CODEC.optionalFieldOf("end_size", DEFAULT_END_SIZE).forGetter(UHCMapConfig::endSize),
            Codec.DOUBLE.optionalFieldOf("shrinking_speed", DEFAULT_SHRINKING_SPEED).forGetter(UHCMapConfig::shrinkingSpeed),
            Codec.INT.optionalFieldOf("spawn_offset", DEFAULT_SPAWN_OFFSET).forGetter(UHCMapConfig::spawnOffset)
    ).apply(instance, UHCMapConfig::new));

    public static UHCMapConfig of(ResourceKey<LevelStem> dimension, DoubleRange startSize, double shrinkingSpeed) {
        return new UHCMapConfig(dimension, startSize, DEFAULT_END_SIZE, shrinkingSpeed, DEFAULT_SPAWN_OFFSET);
    }

    @Override
    public UHCMapConfig clone() {
        return new UHCMapConfig(dimension, startSize, endSize, shrinkingSpeed, spawnOffset);
    }
}
