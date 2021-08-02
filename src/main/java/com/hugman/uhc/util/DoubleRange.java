package com.hugman.uhc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;

public record DoubleRange(double min, double max) {
	public static final Codec<DoubleRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0.0D, Double.MAX_VALUE).fieldOf("min").forGetter(config -> config.min),
			Codec.doubleRange(0.0D, Double.MAX_VALUE).fieldOf("max").forGetter(config -> config.max)
	).apply(instance, DoubleRange::new));


	public double random(Random random) {
		return (random.nextDouble() * (max - min)) + min;
	}

	public double crossProduct(float f) {
		return (int) ((max - min) * f) + min;
	}
}
