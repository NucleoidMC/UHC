package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record UHCChapterConfig(DoubleRange inCagesTime, DoubleRange invulnerabilityTime, DoubleRange warmupTime,
                               DoubleRange deathmatchTime) {
    public static final Codec<UHCChapterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DoubleRange.CODEC.optionalFieldOf("in_cages", new DoubleRange(30, 75)).forGetter(UHCChapterConfig::inCagesTime),
            DoubleRange.CODEC.optionalFieldOf("invulnerability", new DoubleRange(60, 60)).forGetter(UHCChapterConfig::invulnerabilityTime),
            DoubleRange.CODEC.fieldOf("warmup").forGetter(UHCChapterConfig::warmupTime),
            DoubleRange.CODEC.optionalFieldOf("deathmatch", new DoubleRange(2400, 2400)).forGetter(UHCChapterConfig::deathmatchTime)
    ).apply(instance, UHCChapterConfig::new));


}

