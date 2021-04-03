package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldBorderConfig {
	public static final Codec<WorldBorderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("start_size").forGetter(config -> config.startSize),
			Codec.INT.fieldOf("end_size").forGetter(config -> config.endSize),
			Codec.INT.fieldOf("safe_secs").forGetter(config -> config.safeSecs),
			Codec.INT.fieldOf("shrink_secs").forGetter(config -> config.shrinkSecs)
	).apply(instance, WorldBorderConfig::new));

	public final int startSize;
	public final int endSize;
	public final int safeSecs;
	public final int shrinkSecs;

	public WorldBorderConfig(int startSize, int endSize, int safeSecs, int shrinkSecs) {
		this.startSize = startSize;
		this.endSize = endSize;
		this.safeSecs = safeSecs;
		this.shrinkSecs = shrinkSecs;
	}
}
