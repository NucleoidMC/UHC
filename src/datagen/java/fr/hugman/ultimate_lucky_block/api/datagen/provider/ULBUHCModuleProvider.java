package fr.hugman.ultimate_lucky_block.api.datagen.provider;

import fr.hugman.ultimate_lucky_block.api.block.LuckyBlocks;
import fr.hugman.ultimate_lucky_block.api.module.ULBUHCModules;
import fr.hugman.ultimate_lucky_block.api.world.gen.feature.ULBPlacedFeatures;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import fr.hugman.uhc.api.datagen.provider.UHCModuleProvider;
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

public class ULBUHCModuleProvider extends FabricDynamicRegistryProvider {
    public ULBUHCModuleProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(UHCRegistryKeys.UHC_MODULE);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(UltimateLuckyBlock.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "UHC Modules (Lucky)";
    }

    public static void register(Registerable<UHCModule> registerable) {
        final var placedFeatures = registerable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        register(registerable, ULBUHCModules.LUCKY_BLOCKS, LuckyBlocks.LUCKY_BLOCK,
                new PlacedFeaturesModifier(RegistryEntryList.of(
                        placedFeatures.getOrThrow(ULBPlacedFeatures.SURFACE_LUCKY_BLOCKS),
                        placedFeatures.getOrThrow(ULBPlacedFeatures.MINERAL_LUCKY_BLOCKS)
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
