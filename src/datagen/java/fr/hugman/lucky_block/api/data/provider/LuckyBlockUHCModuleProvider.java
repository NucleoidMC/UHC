package fr.hugman.lucky_block.api.data.provider;

import fr.hugman.lucky_block.api.block.LuckyBlocks;
import fr.hugman.lucky_block.api.module.LuckyBlockUHCModules;
import fr.hugman.lucky_block.api.world.gen.feature.LuckyBlockPlacedFeatures;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.api.data.provider.UHCModuleProvider;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.modifier.PlacedFeaturesModifier;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.concurrent.CompletableFuture;

public class LuckyBlockUHCModuleProvider extends FabricDynamicRegistryProvider {
    public LuckyBlockUHCModuleProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(UHCRegistryKeys.UHC_MODULE);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(LuckyBlockMod.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "UHC Modules (Lucky)";
    }

    public static void register(Registerable<UHCModule> registerable) {
        final var placedFeatures = registerable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        register(registerable, LuckyBlockUHCModules.LUCKY_BLOCKS, LuckyBlocks.LUCKY_BLOCK,
                new PlacedFeaturesModifier(RegistryEntryList.of(
                        placedFeatures.getOrThrow(LuckyBlockPlacedFeatures.SURFACE_LUCKY_BLOCKS),
                        placedFeatures.getOrThrow(LuckyBlockPlacedFeatures.MINERAL_LUCKY_BLOCKS)
                ))
        );
    }

    public static void register(
            Registerable<UHCModule> registerable,
            RegistryKey<UHCModule> key,
            ItemConvertible icon,
            Modifier... modifiers
    ) {
        UHCModuleProvider.register(registerable, key, icon, modifiers);
    }
}
