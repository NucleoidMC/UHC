package fr.hugman.lucky_block.api.lucky_event.selector;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventType;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventTypes;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.api.registry.RegistryEntryListBuilder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Triggers a list of lucky events simultaneously.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record AllOfSelectorLuckyEvent(RegistryEntryList<LuckyEvent> events) implements SelectorLuckyEvent {
    public static final MapCodec<AllOfSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codecs.nonEmptyEntryList(LuckyEvent.LIST_CODEC).fieldOf("events").forGetter(AllOfSelectorLuckyEvent::events)
    ).apply(instance, AllOfSelectorLuckyEvent::new));

    public List<RegistryEntry<LuckyEvent>> get(Random random, float luck) {
        return events.stream().toList();
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.ALL_OF_SELECTOR;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends RegistryEntryListBuilder<Builder, LuckyEvent> {
        private Builder() {}

        @Override
        protected Builder getThis() {
            return this;
        }

        public AllOfSelectorLuckyEvent build() {
            return new AllOfSelectorLuckyEvent(RegistryEntryList.of(entries.build()));
        }
    }
}
