package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class UHCMapConfig {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("dimension", DimensionType.OVERWORLD_ID).forGetter(config -> config.dimension),
			Identifier.CODEC.fieldOf("settings").forGetter(config -> config.chunkSettings)
	).apply(instance, UHCMapConfig::new));

	private final Identifier dimension;
	private final Identifier chunkSettings;

	public UHCMapConfig(Identifier dimension, Identifier chunkSettings) {
		this.dimension = dimension;
		this.chunkSettings = chunkSettings;
	}

	public Identifier getChunkSettings() {
		return chunkSettings;
	}

	public Identifier getDimension() {
		return dimension;
	}
}
