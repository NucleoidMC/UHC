package fr.hugman.lucky_block.api.lucky_event.selector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventType;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventTypes;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
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
 * TODO
 *
 * @author Hugman
 * @since 1.0.0
 */
public record LuckSelectorLuckyEvent(List<Entry> entries) implements SelectorLuckyEvent {
    public static final MapCodec<LuckSelectorLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("entries").forGetter(LuckSelectorLuckyEvent::entries)
    ).apply(instance, LuckSelectorLuckyEvent::new));

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
                LuckyBlockMod.LOGGER.info(entry.quality);
                return Collections.singletonList(entry.event());
            }
        }

        return List.of();
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.LUCK_SELECTOR;
    }

    record Entry(RegistryEntry<LuckyEvent> event, int quality) {
        private static final int DEFAULT_QUALITY = 0;

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LuckyEvent.ENTRY_CODEC.fieldOf("event").forGetter(Entry::event),
                Codecs.NON_NEGATIVE_INT.fieldOf("quality").orElse(DEFAULT_QUALITY).forGetter(Entry::quality)
        ).apply(instance, Entry::new));

        public int getWeight(float luck) {
            return Math.max(MathHelper.floor((1 + quality * (luck))), 0);
        }

    }

    public static Builder builder(RegistryEntryLookup<LuckyEvent> registry) {
        return new Builder(registry);
    }

    public static Builder builder(DynamicRegistryManager drm) {
        return builder(drm.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT));
    }

    public static class Builder {
        private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();
        private final RegistryEntryLookup<LuckyEvent> lookup;

        public Builder(RegistryEntryLookup<LuckyEvent> lookup) {
            this.lookup = lookup;
        }

        public Builder oneOf(int quality, TagKey<LuckyEvent> tag) {
            this.entries.add(new Entry(RegistryEntry.of(new OneOfSelectorLuckyEvent(lookup.getOrThrow(tag))), quality));
            return this;
        }

        public LuckSelectorLuckyEvent build() {
            return new LuckSelectorLuckyEvent(entries.build());
        }
    }
}
