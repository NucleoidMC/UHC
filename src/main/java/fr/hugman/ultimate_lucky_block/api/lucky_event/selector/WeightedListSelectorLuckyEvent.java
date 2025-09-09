package fr.hugman.ultimate_lucky_block.api.lucky_event.selector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEventType;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEventTypes;
import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collections;
import java.util.List;

/**
 * Triggers a lucky event randomly based on a weighted list of entries that can be impacted by luck.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record WeightedListSelectorLuckyEvent(List<Entry> entries) implements SelectorLuckyEvent {
    public static final MapCodec<WeightedListSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("entries").forGetter(WeightedListSelectorLuckyEvent::entries)
    ).apply(instance, WeightedListSelectorLuckyEvent::new));

    @Override
    public List<RegistryEntry<LuckyEvent>> get(Random random, float luck) {
        var list = Lists.<Entry>newArrayList();
        var mutableInt = new MutableInt();

        for (var entry : entries) {
            int weight = entry.getWeight(luck);
            if (weight > 0) {
                mutableInt.add(weight);
                list.add(entry);
            }
        }

        var listSize = list.size();
        if (listSize == 0 || mutableInt.getValue() <= 0) {
            return List.of();
        }

        if (listSize == 1) {
            return Collections.singletonList(list.getFirst().event());
        }

        int randomValue = random.nextInt(mutableInt.getValue());
        for (var entry : list) {
            randomValue -= entry.getWeight(luck);
            if (randomValue < 0) {
                return Collections.singletonList(entry.event());
            }
        }

        return List.of();
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.WEIGHTED_LIST_SELECTOR;
    }

    record Entry(RegistryEntry<LuckyEvent> event, int weight, int quality) {
        private static final int DEFAULT_WEIGHT = 1;
        private static final int DEFAULT_QUALITY = 0;

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LuckyEvent.ENTRY_CODEC.fieldOf("event").forGetter(Entry::event),
                Codecs.POSITIVE_INT.optionalFieldOf("weight", DEFAULT_WEIGHT).orElse(DEFAULT_QUALITY).forGetter(Entry::weight),
                Codecs.NON_NEGATIVE_INT.optionalFieldOf("quality", DEFAULT_QUALITY).orElse(DEFAULT_QUALITY).forGetter(Entry::quality)
        ).apply(instance, Entry::new));

        public int getWeight(float luck) {
            return Math.max(MathHelper.floor((weight + quality * (luck))), 0);
        }
    }

    public static Builder builder(RegistryEntryLookup<LuckyEvent> registry) {
        return new Builder(registry);
    }

    public static Builder builder(DynamicRegistryManager drm) {
        return builder(drm.getOrThrow(ULBRegistryKeys.LUCKY_EVENT));
    }

    public static class Builder {
        private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();
        private final RegistryEntryLookup<LuckyEvent> lookup;

        private Builder(RegistryEntryLookup<LuckyEvent> lookup) {
            this.lookup = lookup;
        }

        public Builder add(int weight, int quality, RegistryEntry<LuckyEvent> entry) {
            this.entries.add(new Entry(entry, weight, quality));
            return this;
        }

        public Builder add(int weight, int quality, LuckyEvent event) {
            return this.add(weight, quality, RegistryEntry.of(event));
        }

        public Builder add(int weight, int quality, LuckyEvent... events) {
            return this.add(weight, quality, OneOfSelectorLuckyEvent.builder().add(events).build());
        }

        public Builder add(int weight, int quality, TagKey<LuckyEvent> tag) {
            return this.add(weight, quality, new OneOfSelectorLuckyEvent(lookup.getOrThrow(tag)));
        }

        public WeightedListSelectorLuckyEvent build() {
            return new WeightedListSelectorLuckyEvent(entries.build());
        }
    }
}
