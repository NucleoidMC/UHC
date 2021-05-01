package com.hugman.uhc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;

public class DoubleRange {
	public static final Codec<DoubleRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0.0D, Double.MAX_VALUE).fieldOf("max").forGetter(config -> config.max),
			Codec.doubleRange(0.0D, Double.MAX_VALUE).fieldOf("min").forGetter(config -> config.min)
	).apply(instance, DoubleRange::new));

	private final double max;
	private final double min;

	public DoubleRange(double max, double min) {
		this.max = min;
		this.min = min;
	}

	public double max() {
		return max;
	}

	public double min() {
		return min;
	}

	public double random(Random random) {
		return (random.nextDouble() * (max - min)) + min;
	}

	public double crossProduct(float f) {
		return (int) ((max - min) * f) + min;
	}
}
