package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.util.BlockTraversal;

public record TraversalBreakModifier(RuleTest predicate, int amount, boolean includeLeaves) implements Modifier {
    public static final MapCodec<TraversalBreakModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RuleTest.TYPE_CODEC.fieldOf("target").forGetter(module -> module.predicate),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amount", 128).forGetter(module -> module.amount),
            Codec.BOOL.optionalFieldOf("include_leaves", false).forGetter(module -> module.includeLeaves)
    ).apply(instance, TraversalBreakModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.TRAVERSAL_BREAK;
    }

    public void breakBlock(ServerWorld world, @Nullable LivingEntity entity, BlockPos origin) {
        BlockState state = world.getBlockState(origin);
        var originLong = origin.asLong();

        if (this.predicate.test(state, world.getRandom())) {
            BlockTraversal traversal = BlockTraversal.create()
                    .order(BlockTraversal.Order.BREADTH_FIRST)
                    .connectivity(BlockTraversal.Connectivity.TWENTY_SIX);
            LongSet posLongSet = new LongArraySet();
            traversal.accept(origin, (nextPos, fromPos, depth) -> {
                var nextPosLong = nextPos.asLong();
                if (depth > this.amount) {
                    return BlockTraversal.Result.TERMINATE;
                }
                if (origin.asLong() == nextPosLong || posLongSet.contains(nextPosLong)) {
                    return BlockTraversal.Result.CONTINUE;
                }
                if (this.predicate.test(world.getBlockState(nextPos), world.getRandom())) {
                    posLongSet.add(nextPos.asLong());
                    return BlockTraversal.Result.CONTINUE;
                }
                return BlockTraversal.Result.TERMINATE;
            });
            if (includeLeaves) {
                LongSet leavesLongSet = new LongArraySet();
                BlockTraversal leavesTraversal = BlockTraversal.create()
                        .order(BlockTraversal.Order.BREADTH_FIRST)
                        .connectivity(BlockTraversal.Connectivity.SIX);

                posLongSet.add(originLong);
                for (var posLong : posLongSet) {
                    BlockPos pos = BlockPos.fromLong(posLong);
                    leavesTraversal.accept(pos, (nextPos, fromPos, depth) -> {
                        var nextPosLong = nextPos.asLong();
                        if (depth > this.amount) {
                            return BlockTraversal.Result.TERMINATE;
                        }
                        if (pos.asLong() == nextPosLong) {
                            return BlockTraversal.Result.CONTINUE;
                        }
                        if (posLongSet.contains(nextPosLong) || leavesLongSet.contains(nextPosLong)) {
                            return BlockTraversal.Result.CONTINUE;
                        }
                        BlockState fromState = world.getBlockState(fromPos);
                        BlockState nextState = world.getBlockState(nextPos);
                        if (nextState.contains(LeavesBlock.DISTANCE) && nextState.isIn(BlockTags.LEAVES)) {
                            var currentDistance = fromState.contains(LeavesBlock.DISTANCE) ? fromState.get(LeavesBlock.DISTANCE) : 0;
                            if (nextState.get(LeavesBlock.DISTANCE) > currentDistance) {
                                leavesLongSet.add(nextPos.asLong());
                                return BlockTraversal.Result.CONTINUE;
                            }
                        }
                        return BlockTraversal.Result.TERMINATE;
                    });
                    posLongSet.addAll(leavesLongSet);
                }
                posLongSet.remove(originLong);
            }
            posLongSet.forEach(value -> world.breakBlock(BlockPos.fromLong(value), true, entity));
        }
    }
}
