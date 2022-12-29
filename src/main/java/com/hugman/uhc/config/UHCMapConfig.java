package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.dimension.DimensionOptions;


public record UHCMapConfig(DimensionOptions dimension, DoubleRange startSize, DoubleRange endSize,
						   double shrinkingSpeed, int spawnOffset) {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DimensionOptions.CODEC.fieldOf("dimension").forGetter(UHCMapConfig::dimension),
			DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::startSize),
			DoubleRange.CODEC.optionalFieldOf("end_size", new DoubleRange(15, 40)).forGetter(UHCMapConfig::endSize),
			Codec.DOUBLE.optionalFieldOf("shrinking_speed", 1.0D).forGetter(UHCMapConfig::shrinkingSpeed),
			Codec.INT.optionalFieldOf("spawn_offset", 40).forGetter(UHCMapConfig::spawnOffset)
	).apply(instance, UHCMapConfig::new));
}
