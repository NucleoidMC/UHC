package com.hugman.lucky_block.lucky_event.provider;

import com.hugman.lucky_block.lucky_event.LuckyEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provides lucky events.
 *
 * @author Hugman
 * @since 1.0.0
 */
public interface LuckyEventProvider {
    List<RegistryEntry<LuckyEvent>> get(Random random);

    default void triggerEvents(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        for (RegistryEntry<LuckyEvent> event : get(world.random)) {
            event.value().trigger(world, player, pos, state, blockEntity);
        }
    }
}
