package com.hugman.lucky_block.lucky_event.provider;

import com.hugman.lucky_block.lucky_event.LuckyEvent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Provides a single lucky event.
 *
 * @since 1.0.0
 *
 * @author Hugman
 */
public record SingleLuckyEventProvider(RegistryEntry<LuckyEvent> event) implements LuckyEventProvider {
    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random) {
        return List.of(event);
    }
}
