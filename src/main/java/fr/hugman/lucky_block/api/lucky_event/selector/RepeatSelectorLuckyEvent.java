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
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Triggers a lucky event a specified number of times.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record RepeatSelectorLuckyEvent(
        IntProvider count,
        RegistryEntry<LuckyEvent> event
) implements SelectorLuckyEvent {
    public static final MapCodec<RepeatSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntProvider.POSITIVE_CODEC.fieldOf("count").forGetter(RepeatSelectorLuckyEvent::count),
            LuckyEvent.ENTRY_CODEC.fieldOf("event").forGetter(RepeatSelectorLuckyEvent::event)
    ).apply(instance, RepeatSelectorLuckyEvent::new));

    public RepeatSelectorLuckyEvent(int count, RegistryEntry<LuckyEvent> event) {
        this(ConstantIntProvider.create(count), event);
    }

    public RepeatSelectorLuckyEvent(int count, LuckyEvent event) {
        this(count, RegistryEntry.of(event));
    }

    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random, float luck) {
        var events = ImmutableList.<RegistryEntry<LuckyEvent>>builder();
        for (int i = 0, n = count.get(random); i < n; i++) {
            events.add(event);
        }
        return events.build();
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.REPEAT_SELECTOR;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends RegistryEntryListBuilder<Builder, LuckyEvent> {
        private IntProvider count = ConstantIntProvider.create(1);

        private Builder() {}

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder count(IntProvider count) {
            this.count = count;
            return this;
        }

        public Builder count(int count) {
            return count(ConstantIntProvider.create(count));
        }

        public Builder count(int min, int max) {
            return count(UniformIntProvider.create(min, max));
        }

        public RepeatSelectorLuckyEvent build() {
            var list = entries.build();
            if (list.size() == 1) {
                return new RepeatSelectorLuckyEvent(count, list.getFirst());
            }
            return new RepeatSelectorLuckyEvent(count, RegistryEntry.of(
                    OneOfSelectorLuckyEvent.builder().add(list).build()
            ));
        }
    }
}
