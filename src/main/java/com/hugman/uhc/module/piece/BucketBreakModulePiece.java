package com.hugman.uhc.module.piece;

import com.hugman.uhc.util.BucketFind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.event.BreakBlockListener;

public class BucketBreakModulePiece implements ModulePiece {
	public static final Codec<BucketBreakModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RuleTest.field_25012.fieldOf("predicate").forGetter(module -> module.predicate),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("depth", 64).forGetter(module -> module.depth)
	).apply(instance, BucketBreakModulePiece::new));

	public final RuleTest predicate;
	public final int depth;

	public BucketBreakModulePiece(RuleTest predicate, int depth) {
		this.predicate = predicate;
		this.depth = depth;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	@Override
	public void init(GameLogic game) {
		game.on(BreakBlockListener.EVENT, this::breakBlock);
	}

	public ActionResult breakBlock(ServerPlayerEntity player, BlockPos origin) {
		ServerWorld world = player.getServerWorld();
		BlockState state = world.getBlockState(origin);

		if(this.predicate.test(state, world.getRandom())) {
			LongSet positions = BucketFind.findTwentySix(world, origin, this.depth, this.predicate, world.getRandom());

			for(long l : positions) {
				if(l != origin.asLong()) {
					BlockPos pos = BlockPos.fromLong(l);
					world.breakBlock(pos, true);
				}
			}
		}

		return ActionResult.SUCCESS;
	}

}
