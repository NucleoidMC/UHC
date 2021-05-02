package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;

public class LootReplaceModulePiece implements ModulePiece {
	public static final Codec<LootReplaceModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("predicate").forGetter(module -> module.predicate),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("experience",0).forGetter(module -> module.experience)
	).apply(instance, LootReplaceModulePiece::new));

	private final RuleTest predicate;
	private final Identifier lootTable;
	private final int experience;

	public LootReplaceModulePiece(RuleTest predicate, Identifier lootTable, int experience) {
		this.predicate = predicate;
		this.lootTable = lootTable;
		this.experience = experience;
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

	public int getExperience() {
		return experience;
	}
}
