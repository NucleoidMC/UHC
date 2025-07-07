package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.util.DoubleRange;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptions;

public record UHCMapConfig(
        RegistryKey<DimensionOptions> dimension,
        DoubleRange startSize,
        DoubleRange endSize,
        double shrinkingSpeed,
        int spawnOffset
) {
    private static final DoubleRange DEFAULT_END_SIZE = new DoubleRange(15, 40);
    private static final double DEFAULT_SHRINKING_SPEED = 1.0D;
    private static final int DEFAULT_SPAWN_OFFSET = 40;

    public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.DIMENSION).fieldOf("dimension").forGetter(UHCMapConfig::dimension),
            DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::startSize),
            DoubleRange.CODEC.optionalFieldOf("end_size", DEFAULT_END_SIZE).forGetter(UHCMapConfig::endSize),
            Codec.DOUBLE.optionalFieldOf("shrinking_speed", DEFAULT_SHRINKING_SPEED).forGetter(UHCMapConfig::shrinkingSpeed),
            Codec.INT.optionalFieldOf("spawn_offset", DEFAULT_SPAWN_OFFSET).forGetter(UHCMapConfig::spawnOffset)
    ).apply(instance, UHCMapConfig::new));

    public static UHCMapConfig of(RegistryKey<DimensionOptions> dimension, DoubleRange startSize, double shrinkingSpeed) {
        return new UHCMapConfig(dimension, startSize, DEFAULT_END_SIZE, shrinkingSpeed, DEFAULT_SPAWN_OFFSET);
    }
}
