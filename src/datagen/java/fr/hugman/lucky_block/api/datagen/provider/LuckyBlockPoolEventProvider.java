package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventTags;
import fr.hugman.lucky_block.api.lucky_event.LuckyPoolEvents;
import fr.hugman.lucky_block.api.lucky_event.selector.WeightedListSelectorLuckyEvent;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockPoolEventProvider extends FabricDynamicRegistryProvider {
    public LuckyBlockPoolEventProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(LuckyBlockMod.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Lucky Events (Pools)";
    }


    public static void register(Registerable<LuckyEvent> registerable) {
        var events = registerable.getRegistryLookup(LuckyBlockRegistryKeys.LUCKY_EVENT);

        // Pools
        registerable.register(LuckyPoolEvents.NORMAL, WeightedListSelectorLuckyEvent.builder(events)
                .add(3, 0, LuckyEventTags.VERY_UNLUCKY)
                .add(15, 2, LuckyEventTags.UNLUCKY)
                .add(20, 5, LuckyEventTags.NORMAL)
                .add(15, 7, LuckyEventTags.LUCKY)
                .add(3, 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
    }
}
