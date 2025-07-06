package fr.hugman.lucky_block.api.lucky_event.selector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventType;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventTypes;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event that drops the content of a loot table.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record OneOfSelectorLuckyEvent(RegistryEntryList<LuckyEvent> events) implements LuckyEvent {
    public static final MapCodec<OneOfSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codecs.nonEmptyEntryList(LuckyEvent.LIST_CODEC).fieldOf("events").forGetter(OneOfSelectorLuckyEvent::events)
    ).apply(instance, OneOfSelectorLuckyEvent::new));

    public OneOfSelectorLuckyEvent(DynamicRegistryManager registryLookup, TagKey<LuckyEvent> tag) {
        this(registryLookup.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT).getOrThrow(tag));
    }

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        events.getRandom(world.random).ifPresent(entry -> entry.value().trigger(world, player, pos, state, blockEntity));
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.ONE_OF_SELECTOR;
    }
}
