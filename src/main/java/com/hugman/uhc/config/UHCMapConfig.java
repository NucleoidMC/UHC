package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class UHCMapConfig {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("dimension", DimensionType.OVERWORLD_ID).forGetter(config -> config.dimension),
			WorldBorderConfig.CODEC.fieldOf("border").forGetter(config -> config.borderConfig),
			Identifier.CODEC.fieldOf("settings").forGetter(config -> config.chunkSettings)
	).apply(instance, UHCMapConfig::new));

	public final Identifier dimension;
	public final WorldBorderConfig borderConfig;
	public final Identifier chunkSettings;

	public UHCMapConfig(Identifier dimension, WorldBorderConfig borderConfig, Identifier chunkSettings) {
		this.dimension = dimension;
		this.borderConfig = borderConfig;
		this.chunkSettings = chunkSettings;
	}
}
