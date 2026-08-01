package fr.hugman.uhc.api.config;

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

    public UHCTimersConfig withCages(double cages) {
        return new UHCTimersConfig(cages, this.invulnerability, this.warmup, this.deathmatch);
    }

    public UHCTimersConfig withInvulnerability(double invulnerability) {
        return new UHCTimersConfig(this.cages, invulnerability, this.warmup, this.deathmatch);
    }

    public UHCTimersConfig withWarmup(double warmup) {
        return new UHCTimersConfig(this.cages, this.invulnerability, warmup, this.deathmatch);
    }

    public UHCTimersConfig withDeathmatch(double deathmatch) {
        return new UHCTimersConfig(this.cages, this.invulnerability, this.warmup, deathmatch);
    }

    @Override
    public UHCTimersConfig clone() {
        return new UHCTimersConfig(cages, invulnerability, warmup, deathmatch);
    }
}

