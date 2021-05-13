package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;

import java.util.Collections;
import java.util.List;

public class BlockLootModulePiece implements ModulePiece {
	public static final Codec<BlockLootModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("target").forGetter(module -> module.predicate),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("experience", 0).forGetter(module -> module.experience)
	).apply(instance, BlockLootModulePiece::new));

	private final RuleTest predicate;
	private final Identifier lootTable;
	private final int experience;

	public BlockLootModulePiece(RuleTest predicate, Identifier lootTable, int experience) {
		this.predicate = predicate;
		this.lootTable = lootTable;
		this.experience = experience;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public boolean breakBlock(GameSpace gameSpace, @Nullable ServerPlayerEntity player, BlockPos pos) {
		ServerWorld world = gameSpace.getWorld();
		BlockState state = world.getBlockState(pos);

		if(this.predicate.test(state, world.getRandom())) {
			LootContext.Builder builder = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, player == null ? ItemStack.EMPTY : player.getMainHandStack()).optionalParameter(LootContextParameters.THIS_ENTITY, player).optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));
			List<ItemStack> stacks;
			if(this.lootTable == LootTables.EMPTY) {
				stacks = Collections.emptyList();
			}
			else {
				LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
				LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(this.lootTable);
				stacks = lootTable.generateLoot(lootContext);
			}
			int xp = this.experience;
			while(xp > 0) {
				int i = ExperienceOrbEntity.roundToOrbSize(xp);
				xp -= i;
				world.spawnEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i));
			}
			stacks.forEach((stack) -> Block.dropStack(world, pos, stack));
			world.removeBlock(pos, false);
			return true;
		}
		else return false;
	}

}
