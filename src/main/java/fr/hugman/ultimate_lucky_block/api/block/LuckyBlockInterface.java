package fr.hugman.ultimate_lucky_block.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * @author Hugman
 * @since 1.0.0
 */
public interface LuckyBlockInterface {
    void onLuckyBlockTrigger(ServerWorld world, @Nullable PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
}
