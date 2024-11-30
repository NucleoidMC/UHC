package com.hugman.lucky_block.lucky_event.provider;

import com.hugman.lucky_block.lucky_event.LuckyEvent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Provides all lucky events in a list.
 *
 * @since 1.0.0
 *
 * @author Hugman
 */
public record AllOfLuckyEventProvider(RegistryEntryList<LuckyEvent> events) implements LuckyEventProvider {
    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random) {
        return events.stream().toList();
    }
}
