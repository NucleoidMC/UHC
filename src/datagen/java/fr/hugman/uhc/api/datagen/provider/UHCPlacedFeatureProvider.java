package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.world.level.levelgen.feature.UHCConfiguredFeatures;
import fr.hugman.uhc.api.world.level.levelgen.feature.UHCPlacedFeatures;
import fr.hugman.ultimate_lucky_block.api.world.gen.feature.ULBPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// WARNING: NEVER PUT BIOME MODIFIERS, THEY ARE NOT SUPPORTED IN UHC
public class UHCPlacedFeatureProvider extends FabricDynamicRegistryProvider {
    public UHCPlacedFeatureProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return "Placed Features";
    }


    public static void register(BootstrapContext<PlacedFeature> registerable) {
        final var configured = registerable.lookup(Registries.CONFIGURED_FEATURE);

        var fullRangePlacement = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.belowTop(0));

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

        // TEMPORARY FIX FOR DATAGEN (fake values)
        registerable.register(ULBPlacedFeatures.SURFACE_LUCKY_BLOCKS, PlacementUtils.inlinePlaced(configured.getOrThrow(VegetationFeatures.GRASS)).value());
        registerable.register(ULBPlacedFeatures.MINERAL_LUCKY_BLOCKS, PlacementUtils.inlinePlaced(configured.getOrThrow(VegetationFeatures.GRASS)).value());
    }

    public static void of(
            BootstrapContext<PlacedFeature> featureRegisterable,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> feature,
            List<PlacementModifier> modifiers
    ) {
        PlacementUtils.register(featureRegisterable, key, feature, modifiers);
    }

    public static void of(
            BootstrapContext<PlacedFeature> featureRegisterable,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> feature,
            PlacementModifier... modifiers
    ) {
        PlacementUtils.register(featureRegisterable, key, feature, modifiers);
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return modifiers(CountPlacement.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilter.onAverageOnceEvery(chance), heightModifier);
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, InSquarePlacement.spread(), heightModifier);
    }
}
