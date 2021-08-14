package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;


public record UHCMapConfig(Identifier dimension, ChunkGeneratorSettings chunkSettings, DoubleRange startSize, DoubleRange endSize, double shrinkingSpeed, int spawnOffset) {
	public static final Codec<UHCMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("dimension", DimensionType.OVERWORLD_ID).forGetter(UHCMapConfig::dimension),
			BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.fieldOf("settings").forGetter(UHCMapConfig::chunkSettings),
			DoubleRange.CODEC.fieldOf("start_size").forGetter(UHCMapConfig::startSize),
			DoubleRange.CODEC.optionalFieldOf("end_size", new DoubleRange(15, 40)).forGetter(UHCMapConfig::endSize),
			Codec.DOUBLE.optionalFieldOf("shrinking_speed", 1.0D).forGetter(UHCMapConfig::shrinkingSpeed),
			Codec.INT.optionalFieldOf("spawn_offset", 40).forGetter(UHCMapConfig::spawnOffset)
	).apply(instance, UHCMapConfig::new));
}
