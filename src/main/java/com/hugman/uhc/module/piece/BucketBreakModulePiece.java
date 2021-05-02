package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.rule.RuleTest;

public class BucketBreakModulePiece implements ModulePiece {
	public static final Codec<BucketBreakModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("predicate").forGetter(module -> module.predicate),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("depth", 64).forGetter(module -> module.depth)
	).apply(instance, BucketBreakModulePiece::new));

	private final RuleTest predicate;
	private final int depth;

	public BucketBreakModulePiece(RuleTest predicate, int depth) {
		this.predicate = predicate;
		this.depth = depth;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public RuleTest getPredicate() {
		return predicate;
	}

	public int getDepth() {
		return depth;
	}
}
