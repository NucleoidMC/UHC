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
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Triggers a lucky event randomly selected from a list.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record OneOfSelectorLuckyEvent(RegistryEntryList<LuckyEvent> events) implements SelectorLuckyEvent {
    public static final MapCodec<OneOfSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codecs.nonEmptyEntryList(LuckyEvent.LIST_CODEC).fieldOf("events").forGetter(OneOfSelectorLuckyEvent::events)
    ).apply(instance, OneOfSelectorLuckyEvent::new));

    public static OneOfSelectorLuckyEvent of(Registry<LuckyEvent> registry, TagKey<LuckyEvent> tag) {
        return new OneOfSelectorLuckyEvent(registry.getOrThrow(tag));
    }

    public static OneOfSelectorLuckyEvent oneOf(DynamicRegistryManager registryLookup, TagKey<LuckyEvent> tag) {
        return of(registryLookup.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT), tag);
    }

    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random, float luck) {
        return events.getRandom(random).map(Collections::singletonList).orElse(Collections.emptyList());
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.ONE_OF_SELECTOR;
    }
}
