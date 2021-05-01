package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UHCTimeConfig {
	public static final Codec<UHCTimeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DoubleRange.CODEC.fieldOf("in_cages").forGetter(UHCTimeConfig::getInCagesTime),
			DoubleRange.CODEC.fieldOf("invulnerability").forGetter(UHCTimeConfig::getInvulnerabilityTime),
			DoubleRange.CODEC.fieldOf("preparation").forGetter(UHCTimeConfig::getSetupTime),
			DoubleRange.CODEC.fieldOf("shrinking").forGetter(UHCTimeConfig::getShrinkingTime)
	).apply(instance, UHCTimeConfig::new));

	private final DoubleRange inCages;
	private final DoubleRange invulnerability;
	private final DoubleRange setup;
	private final DoubleRange shrinking;

	public UHCTimeConfig(DoubleRange inCages, DoubleRange invulnerability, DoubleRange setup, DoubleRange shrinking) {
		this.inCages = inCages;
		this.invulnerability = invulnerability;
		this.setup = setup;
		this.shrinking = shrinking;
	}

	public DoubleRange getInCagesTime() {
		return inCages;
	}

	public DoubleRange getInvulnerabilityTime() {
		return invulnerability;
	}

	public DoubleRange getSetupTime() {
		return setup;
	}

	public DoubleRange getShrinkingTime() {
		return shrinking;
	}
}
