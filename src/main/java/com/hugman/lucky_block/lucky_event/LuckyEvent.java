package com.hugman.lucky_block.lucky_event;

import com.hugman.lucky_block.registry.LuckyBlockRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event, triggers something in the world.
 *
 * @since 1.0.0
 *
 * @author Hugman
 */
public interface LuckyEvent {
    Codec<LuckyEvent> TYPE_CODEC = LuckyBlockRegistries.LUCKY_EVENT_TYPE.getCodec().dispatch(LuckyEvent::getType, LuckyEventType::codec);

    LuckyEventType<?> getType();

    void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
}
