package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BlockLootModifier implements Modifier {
	public static final Codec<BlockLootModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", true).forGetter(module -> module.replace),
			RuleTest.TYPE_CODEC.fieldOf("target").forGetter(module -> module.predicate),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("experience", 0).forGetter(module -> module.experience)
	).apply(instance, BlockLootModifier::new));

	private final boolean replace;
	private final RuleTest predicate;
	private final Identifier lootTable;
	private final int experience;

	private BlockLootModifier(boolean replace, RuleTest predicate, Identifier lootTable, int experience) {
		this.replace = replace;
		this.predicate = predicate;
		this.lootTable = lootTable;
		this.experience = experience;
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierType.BLOCK_LOOT;
	}

	public boolean test(BlockState state, Random random) {
		return this.predicate.test(state, random);
	}

	public void spawnExperience(ServerWorld world, BlockPos pos) {
		int xp = this.experience;
		while (xp > 0) {
			int i = ExperienceOrbEntity.roundToOrbSize(xp);
			xp -= i;
			world.spawnEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i));
		}
	}

	public List<ItemStack> getLoots(ServerWorld world, BlockPos pos, @Nullable Entity entity, ItemStack stack) {
		LootContext.Builder builder = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, stack).optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));
		if (entity != null) {
			builder = builder.optionalParameter(LootContextParameters.THIS_ENTITY, entity);
		}
		List<ItemStack> stacks = Collections.emptyList();
		if (this.lootTable != LootTables.EMPTY) {
			LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, world.getBlockState(pos)).build(LootContextTypes.BLOCK);
			LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(this.lootTable);
			stacks = lootTable.generateLoot(lootContext);
		}
		return stacks;
	}

	public boolean shouldReplace() {
		return replace;
	}
}
