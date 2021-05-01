package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class UHCMapConfig {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("dimension", DimensionType.OVERWORLD_ID).forGetter(UHCMapConfig::getDimension),
			Identifier.CODEC.fieldOf("settings").forGetter(UHCMapConfig::getChunkSettings),
			DoubleRange.CODEC.fieldOf("max_size").forGetter(UHCMapConfig::getMaxSize),
			DoubleRange.CODEC.fieldOf("min_size").forGetter(UHCMapConfig::getMinSize)
	).apply(instance, UHCMapConfig::new));

	private final Identifier dimension;
	private final Identifier chunkSettings;
	private final DoubleRange maxSize;
	private final DoubleRange minSize;

	public UHCMapConfig(Identifier dimension, Identifier chunkSettings, DoubleRange maxSize, DoubleRange minSize) {
		this.dimension = dimension;
		this.chunkSettings = chunkSettings;
		this.maxSize = maxSize;
		this.minSize = minSize;
	}

	public Identifier getChunkSettings() {
		return chunkSettings;
	}

	public Identifier getDimension() {
		return dimension;
	}

	public DoubleRange getMaxSize() {
		return maxSize;
	}

	public DoubleRange getMinSize() {
		return minSize;
	}
}
