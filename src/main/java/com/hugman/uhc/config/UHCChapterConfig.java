package com.hugman.uhc.config;

import com.hugman.uhc.util.DoubleRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UHCChapterConfig {
	public static final Codec<UHCChapterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DoubleRange.CODEC.optionalFieldOf("in_cages", new DoubleRange(10, 45)).forGetter(UHCChapterConfig::getInCagesTime),
			DoubleRange.CODEC.optionalFieldOf("invulnerability", new DoubleRange(60, 60)).forGetter(UHCChapterConfig::getInvulnerabilityTime),
			DoubleRange.CODEC.fieldOf("peaceful").forGetter(UHCChapterConfig::getInvulnerabilityTime),
			DoubleRange.CODEC.fieldOf("wild").forGetter(UHCChapterConfig::getWildTime),
			DoubleRange.CODEC.optionalFieldOf("deathmatch", new DoubleRange(600, 900)).forGetter(UHCChapterConfig::getDeathmatchTime)
	).apply(instance, UHCChapterConfig::new));

	private final DoubleRange inCages;
	private final DoubleRange invulnerability;
	private final DoubleRange peaceful;
	private final DoubleRange wild;
	private final DoubleRange deathmatch;

	public UHCChapterConfig(DoubleRange inCages, DoubleRange invulnerability, DoubleRange peaceful, DoubleRange wild, DoubleRange deathmatch) {
		this.inCages = inCages;
		this.peaceful = peaceful;
		this.invulnerability = invulnerability;
		this.wild = wild;
		this.deathmatch = deathmatch;
	}

	public DoubleRange getInCagesTime() {
		return inCages;
	}

	public DoubleRange getPeacefulTime() {
		return peaceful;
	}

	public DoubleRange getInvulnerabilityTime() {
		return invulnerability;
	}

	public DoubleRange getWildTime() {
		return wild;
	}

	public DoubleRange getDeathmatchTime() {
		return deathmatch;
	}
}
