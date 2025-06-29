package fr.hugman.uhc.data.provider;

import fr.hugman.uhc.api.world.gen.feature.UHCConfiguredFeatures;
import fr.hugman.uhc.api.world.gen.feature.UHCPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// WARNING: NEVER PUT BIOME MODIFIERS, THEY ARE NOT SUPPORTED IN UHC
public class UHCPlacedFeatureProvider extends FabricDynamicRegistryProvider {
    public UHCPlacedFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return "Placed Features";
    }


    public static void register(Registerable<PlacedFeature> registerable) {
        final var configured = registerable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        var fullRangePlacement = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(0), YOffset.belowTop(0));

        // Boosted Ores
        var lapis = configured.getOrThrow(UHCConfiguredFeatures.BOOSTED_LAPIS);
        var gold = configured.getOrThrow(UHCConfiguredFeatures.BOOSTED_GOLD);
        var diamond = configured.getOrThrow(UHCConfiguredFeatures.BOOSTED_DIAMOND);

        of(registerable, UHCPlacedFeatures.BOOSTED_LAPIS_1, lapis, modifiersWithCount(12, fullRangePlacement));
        of(registerable, UHCPlacedFeatures.BOOSTED_GOLD_1, gold, modifiersWithCount(10, fullRangePlacement));
        of(registerable, UHCPlacedFeatures.BOOSTED_DIAMOND_1, diamond, modifiersWithCount(9, fullRangePlacement));

        of(registerable, UHCPlacedFeatures.BOOSTED_LAPIS_2, lapis, modifiersWithCount(14, fullRangePlacement));
        of(registerable, UHCPlacedFeatures.BOOSTED_GOLD_2, gold, modifiersWithCount(14, fullRangePlacement));
        of(registerable, UHCPlacedFeatures.BOOSTED_DIAMOND_2, diamond, modifiersWithCount(12, fullRangePlacement));
    }

    public static void of(
            Registerable<PlacedFeature> featureRegisterable,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> feature,
            List<PlacementModifier> modifiers
    ) {
        PlacedFeatures.register(featureRegisterable, key, feature, modifiers);
    }

    public static void of(
            Registerable<PlacedFeature> featureRegisterable,
            RegistryKey<PlacedFeature> key,
            RegistryEntry<ConfiguredFeature<?, ?>> feature,
            PlacementModifier... modifiers
    ) {
        PlacedFeatures.register(featureRegisterable, key, feature, modifiers);
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return modifiers(CountPlacementModifier.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier);
    }
}
