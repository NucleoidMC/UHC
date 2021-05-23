package com.hugman.uhc.module.piece;

import com.hugman.uhc.game.phase.UHCActive;
import com.hugman.uhc.util.BucketScanner;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BucketBreakModulePiece implements ModulePiece {
	public static final Codec<BucketBreakModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("target").forGetter(module -> module.predicate),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amount", 128).forGetter(module -> module.amount)
	).apply(instance, BucketBreakModulePiece::new));

	private final RuleTest predicate;
	private final int amount;

	public BucketBreakModulePiece(RuleTest predicate, int amount) {
		this.predicate = predicate;
		this.amount = amount;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public void breakBlock(UHCActive active, @Nullable ServerPlayerEntity player, BlockPos origin) {
		ServerWorld world = active.gameSpace.getWorld();
		BlockState state = world.getBlockState(origin);

		if(this.predicate.test(state, world.getRandom())) {
			LongSet positions = BucketScanner.create((previousPos, pos) -> world.getWorldBorder().contains(pos) && this.predicate.test(world.getBlockState(pos), world.getRandom()))
					.maxAmount(this.amount)
					.connectivity(BucketScanner.Connectivity.TWENTY_SIX)
					.searchType(BucketScanner.SearchType.BREADTH_FIRST)
					.build(origin)
					.getPositions();
			for(long l : positions) {
				if(l != origin.asLong()) {
					BlockPos pos = BlockPos.fromLong(l);
					if(world.getWorldBorder().contains(pos)) {
						world.breakBlock(pos, true, player);
					}
				}
			}
		}
	}
}
