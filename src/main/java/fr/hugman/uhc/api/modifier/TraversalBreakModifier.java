package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.util.BlockTraversal;

public record TraversalBreakModifier(
        RuleTest predicate,
        int amount,
        boolean includeLeaves
) implements Modifier {
    public static final int DEFAULT_AMOUNT = 128;

    public static final MapCodec<TraversalBreakModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RuleTest.CODEC.fieldOf("target").forGetter(module -> module.predicate),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amount", DEFAULT_AMOUNT).forGetter(module -> module.amount),
            Codec.BOOL.optionalFieldOf("include_leaves", false).forGetter(module -> module.includeLeaves)
    ).apply(instance, TraversalBreakModifier::new));

    public TraversalBreakModifier(RuleTest predicate, boolean includeLeaves) {
        this(predicate, DEFAULT_AMOUNT, includeLeaves);
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.TRAVERSAL_BREAK;
    }

    public void breakBlock(ServerLevel level, @Nullable LivingEntity entity, BlockPos origin) {
        BlockState state = level.getBlockState(origin);
        var originLong = origin.asLong();

        if (this.predicate.test(state, level.getRandom())) {
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
                if (this.predicate.test(level.getBlockState(nextPos), level.getRandom())) {
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
                    BlockPos pos = BlockPos.of(posLong);
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
                        BlockState fromState = level.getBlockState(fromPos);
                        BlockState nextState = level.getBlockState(nextPos);
                        if (nextState.hasProperty(LeavesBlock.DISTANCE) && nextState.is(BlockTags.LEAVES)) {
                            var currentDistance = fromState.hasProperty(LeavesBlock.DISTANCE) ? fromState.getValue(LeavesBlock.DISTANCE) : 0;
                            if (nextState.getValue(LeavesBlock.DISTANCE) > currentDistance) {
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
            posLongSet.forEach(value -> level.destroyBlock(BlockPos.of(value), true, entity));
        }
    }
}
