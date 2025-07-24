package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.Codec;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistries;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event, triggers something in the world at a fixed position by a player.
 *
 * <p>Usually triggered by a player breaking a lucky block, but can be used in other contexts as well.
 *
 * @author Hugman
 * @since 1.0.0
 */
public interface LuckyEvent {
    Codec<LuckyEvent> TYPE_CODEC = LuckyBlockRegistries.LUCKY_EVENT_TYPE.getCodec().dispatch(LuckyEvent::getType, LuckyEventType::codec);

    Codec<RegistryEntry<LuckyEvent>> ENTRY_CODEC = RegistryElementCodec.of(LuckyBlockRegistryKeys.LUCKY_EVENT, TYPE_CODEC);
    Codec<RegistryEntryList<LuckyEvent>> LIST_CODEC = RegistryCodecs.entryList(LuckyBlockRegistryKeys.LUCKY_EVENT, TYPE_CODEC);

    LuckyEventType<?> getType();

    void trigger(ServerWorld world, @Nullable PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
}
