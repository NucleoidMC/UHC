package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.util.BlockTraversal;

public class TraversalBreakModifier implements Modifier {
	public static final Codec<TraversalBreakModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.TYPE_CODEC.fieldOf("target").forGetter(module -> module.predicate),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amount", 128).forGetter(module -> module.amount)
	).apply(instance, TraversalBreakModifier::new));

	private final RuleTest predicate;
	private final int amount;

	private TraversalBreakModifier(RuleTest predicate, int amount) {
		this.predicate = predicate;
		this.amount = amount;
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierType.TRAVERSAL_BREAK;
	}

	public void breakBlock(ServerWorld world, @Nullable LivingEntity entity, BlockPos origin) {
		BlockState state = world.getBlockState(origin);

		if (this.predicate.test(state, world.getRandom())) {
			BlockTraversal traversal = BlockTraversal.create()
					.order(BlockTraversal.Order.BREADTH_FIRST)
					.connectivity(BlockTraversal.Connectivity.TWENTY_SIX);
			LongSet blockPosList = new LongArraySet();
			traversal.accept(origin, (nextPos, fromPos, depth) -> {
				if (depth > this.amount) {
					return BlockTraversal.Result.TERMINATE;
				}
				if (origin.asLong() == nextPos.asLong()) {
					return BlockTraversal.Result.CONTINUE;
				}
				BlockState previousState = world.getBlockState(fromPos);
				BlockState nextState = world.getBlockState(nextPos);
				if (this.predicate.test(nextState, world.getRandom())) {
					blockPosList.add(nextPos.asLong());
					return BlockTraversal.Result.CONTINUE;
				} else {
					if (nextState.getBlock() instanceof LeavesBlock) {
						if (!(previousState.getBlock() instanceof LeavesBlock)) {
							if (nextState.get(LeavesBlock.DISTANCE) == 1) {
								blockPosList.add(nextPos.asLong());
								return BlockTraversal.Result.CONTINUE;
							}
						} else {
							if (nextState.get(LeavesBlock.DISTANCE) >= previousState.get(LeavesBlock.DISTANCE)) {
								blockPosList.add(nextPos.asLong());
								return BlockTraversal.Result.CONTINUE;
							}
						}
					}
				}
				return BlockTraversal.Result.TERMINATE;
			});
			blockPosList.longStream().forEach(l -> world.breakBlock(BlockPos.fromLong(l), true, entity));
		}
	}
}
