package com.hugman.lucky_block.lucky_event.provider;

import com.hugman.lucky_block.lucky_event.LuckyEvent;
import com.hugman.lucky_block.lucky_event.LuckyEventTags;
import com.hugman.lucky_block.registry.LuckyBlockRegistryKeys;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Provides a single lucky event from a list with equal probability.
 *
 * @since 1.0.0
 *
 * @author Hugman
 */
public record OneOfLuckyEventProvider(RegistryEntryList<LuckyEvent> events) implements LuckyEventProvider {
    public OneOfLuckyEventProvider(DynamicRegistryManager registryLookup, TagKey<LuckyEvent> tag) {
        this(registryLookup.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT).getOrThrow(tag));
    }

    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random) {
        return events.getRandom(random).map(List::of).orElse(List.of());
    }
}
