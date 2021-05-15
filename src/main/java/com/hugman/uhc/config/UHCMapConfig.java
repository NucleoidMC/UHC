package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class UHCMapConfig {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("dimension", DimensionType.OVERWORLD_ID).forGetter(UHCMapConfig::getDimension),
			Identifier.CODEC.fieldOf("settings").forGetter(UHCMapConfig::getChunkSettingsId),
			DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::getStartSize),
			DoubleRange.CODEC.optionalFieldOf("end_size", new DoubleRange(15, 40)).forGetter(UHCMapConfig::getEndSize),
			Codec.DOUBLE.optionalFieldOf("shrinking_speed", 1.0D).forGetter(UHCMapConfig::getShrinkingSpeed),
			Codec.INT.optionalFieldOf("spawn_offset", 40).forGetter(UHCMapConfig::getSpawnOffset)
	).apply(instance, UHCMapConfig::new));

	private final Identifier dimension;
	private final Identifier chunkSettings;
	private final DoubleRange startSize;
	private final DoubleRange endSize;
	private final double shrinkingSpeed;
	private final int spawnOffset;

	public UHCMapConfig(Identifier dimension, Identifier chunkSettings, DoubleRange startSize, DoubleRange minSize, double shrinkingSpeed, int spawnOffset) {
		this.dimension = dimension;
		this.chunkSettings = chunkSettings;
		this.startSize = startSize;
		this.endSize = minSize;
		this.shrinkingSpeed = shrinkingSpeed;
		this.spawnOffset = spawnOffset;
	}

	public Identifier getChunkSettingsId() {
		return chunkSettings;
	}

	public Identifier getDimension() {
		return dimension;
	}

	public DoubleRange getStartSize() {
		return startSize;
	}

	public DoubleRange getEndSize() {
		return endSize;
	}

	public double getShrinkingSpeed() {
		return shrinkingSpeed;
	}

	public int getSpawnOffset() {
		return spawnOffset;
	}
}
