package fr.hugman.lucky_block.api.lucky_event.selector;

import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
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
 * TODO
 *
 * @author Hugman
 * @since 1.0.0
 */
public interface SelectorLuckyEvent extends LuckyEvent {
    List<RegistryEntry<LuckyEvent>> get(Random random, float luck);

    default void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        float luck = player.getLuck();
        for (RegistryEntry<LuckyEvent> event : get(world.random, luck)) {
            event.value().trigger(world, player, pos, state, blockEntity);
        }
    }
}
