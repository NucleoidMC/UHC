package com.hugman.uhc.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record UHCTimersConfig(
        double cages,
        double invulnerability,
        double warmup,
        double deathmatch
) {
    public static final UHCTimersConfig DEFAULT = new UHCTimersConfig(30.0D, 60.0D, 3600.0D, 2400.0D);

    public static final Codec<UHCTimersConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("in_cages", DEFAULT.cages()).forGetter(UHCTimersConfig::cages),
            Codec.DOUBLE.optionalFieldOf("invulnerability", DEFAULT.invulnerability()).forGetter(UHCTimersConfig::invulnerability),
            Codec.DOUBLE.optionalFieldOf("warmup", DEFAULT.warmup()).forGetter(UHCTimersConfig::warmup),
            Codec.DOUBLE.optionalFieldOf("deathmatch", DEFAULT.deathmatch()).forGetter(UHCTimersConfig::deathmatch)
    ).apply(instance, UHCTimersConfig::new));


}

