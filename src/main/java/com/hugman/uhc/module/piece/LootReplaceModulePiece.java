package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;

public class LootReplaceModulePiece implements ModulePiece {
	public static final Codec<LootReplaceModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("predicate").forGetter(module -> module.predicate),
			Identifier.CODEC.fieldOf("loot_table").forGetter(module -> module.lootTable)
	).apply(instance, LootReplaceModulePiece::new));

	private final RuleTest predicate;
	private final Identifier lootTable;

	public LootReplaceModulePiece(RuleTest predicate, Identifier lootTable) {
		this.predicate = predicate;
		this.lootTable = lootTable;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public RuleTest getPredicate() {
		return predicate;
	}

	public Identifier getLootTable() {
		return lootTable;
	}
}
