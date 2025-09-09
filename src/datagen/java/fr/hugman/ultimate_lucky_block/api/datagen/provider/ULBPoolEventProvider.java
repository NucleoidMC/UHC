package fr.hugman.ultimate_lucky_block.api.datagen.provider;

import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEventTags;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyPoolEvents;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.RepeatSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.WeightedListSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBPoolEventProvider extends FabricDynamicRegistryProvider {
    private static final int[] LUCK_DISTRIBUTION = {3, 15, 10, 15, 3};
    private static final int[] LUCKY_LUCK_DISTRIBUTION = {1, 3, 10, 20, 5};
    private static final int[] VERY_LUCKY_LUCK_DISTRIBUTION = {1, 5, 15, 20};
    private static final int[] UNLUCKY_LUCK_DISTRIBUTION = {5, 20, 10, 3, 1};
    private static final int[] VERY_UNLUCKY_LUCK_DISTRIBUTION = {20, 15, 5, 3};

    public ULBPoolEventProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(ULBRegistryKeys.LUCKY_EVENT);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(UltimateLuckyBlock.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Lucky Events (Pools)";
    }


    public static void register(Registerable<LuckyEvent> registerable) {
        var events = registerable.getRegistryLookup(ULBRegistryKeys.LUCKY_EVENT);

        registerable.register(LuckyPoolEvents.NORMAL, WeightedListSelectorLuckyEvent.builder(events)
                .add(LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.LUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(LUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(LUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.VERY_LUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[0], 2, LuckyEventTags.UNLUCKY)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[1], 5, LuckyEventTags.NORMAL)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[2], 7, LuckyEventTags.LUCKY)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[3], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.UNLUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(UNLUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(UNLUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.VERY_UNLUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.DOUBLE, RepeatSelectorLuckyEvent.builder().count(2).add(events.getOrThrow(LuckyPoolEvents.NORMAL)).build());
        registerable.register(LuckyPoolEvents.TRIPLE, RepeatSelectorLuckyEvent.builder().count(3).add(events.getOrThrow(LuckyPoolEvents.NORMAL)).build());
    }
}
