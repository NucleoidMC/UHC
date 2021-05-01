package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.event.BreakBlockListener;

import java.util.Collections;
import java.util.List;

public class LootReplaceModulePiece implements ModulePiece {
	public static final Codec<LootReplaceModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("predicate").forGetter(module -> module.predicate),
			Identifier.CODEC.fieldOf("loot_table").forGetter(module -> module.lootTable)
	).apply(instance, LootReplaceModulePiece::new));

	public final RuleTest predicate;
	public final Identifier lootTable;

	public LootReplaceModulePiece(RuleTest predicate, Identifier lootTable) {
		this.predicate = predicate;
		this.lootTable = lootTable;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	@Override
	public void init(GameLogic game) {
		game.on(BreakBlockListener.EVENT, this::breakBlock);
	}

	public ActionResult breakBlock(ServerPlayerEntity player, BlockPos pos) {
		ServerWorld world = player.getServerWorld();
		BlockState state = world.getBlockState(pos);
		if(this.predicate.test(state, world.getRandom())) {
			LootContext.Builder builder = (new LootContext.Builder(world)).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, player.getMainHandStack()).optionalParameter(LootContextParameters.THIS_ENTITY, player).optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));
			List<ItemStack> stacks;
			if(this.lootTable == LootTables.EMPTY) {
				stacks = Collections.emptyList();
			}
			else {
				LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
				LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(this.lootTable);
				stacks = lootTable.generateLoot(lootContext);
			}
			stacks.forEach((stack) -> {
				Block.dropStack(world, pos, stack);
			});
			world.breakBlock(pos, false, player);
			return ActionResult.PASS;
		}
		return ActionResult.PASS;
	}
}
