package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UHCChapterConfig {
	public static final Codec<UHCChapterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DoubleRange.CODEC.optionalFieldOf("in_cages", new DoubleRange(30, 75)).forGetter(UHCChapterConfig::getInCagesTime),
			DoubleRange.CODEC.optionalFieldOf("invulnerability", new DoubleRange(60, 60)).forGetter(UHCChapterConfig::getInvulnerabilityTime),
			DoubleRange.CODEC.fieldOf("warmup").forGetter(UHCChapterConfig::getWarmupTime),
			DoubleRange.CODEC.optionalFieldOf("deathmatch", new DoubleRange(2400, 2400)).forGetter(UHCChapterConfig::getDeathmatchTime)
	).apply(instance, UHCChapterConfig::new));

	private final DoubleRange inCages;
	private final DoubleRange invulnerability;
	private final DoubleRange warmup;
	private final DoubleRange deathmatch;

	public UHCChapterConfig(DoubleRange inCages, DoubleRange invulnerability, DoubleRange warmup, DoubleRange deathmatch) {
		this.inCages = inCages;
		this.warmup = warmup;
		this.invulnerability = invulnerability;
		this.deathmatch = deathmatch;
	}

	public DoubleRange getInCagesTime() {
		return inCages;
	}

	public DoubleRange getInvulnerabilityTime() {
		return invulnerability;
	}

	public DoubleRange getWarmupTime() {
		return warmup;
	}

	public DoubleRange getDeathmatchTime() {
		return deathmatch;
	}
}
