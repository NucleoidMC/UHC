package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.world.gen.feature.LuckyBlockConfiguredFeatures;
import fr.hugman.lucky_block.api.world.gen.feature.LuckyBlockPlacedFeatures;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockPlacedFeatureProvider extends FabricDynamicRegistryProvider {
    public LuckyBlockPlacedFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(RegistryKeys.PLACED_FEATURE);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(LuckyBlockMod.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Placed Features (Lucky)";
    }


    public static void register(Registerable<PlacedFeature> registerable) {
        final var configured = registerable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        var fullRangePlacement = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(0), YOffset.belowTop(0));

        of(registerable, LuckyBlockPlacedFeatures.MINERAL_LUCKY_BLOCKS, configured.getOrThrow(LuckyBlockConfiguredFeatures.MINERAL_LUCKY_BLOCKS),
                modifiersWithCount(64, fullRangePlacement)
        );
        of(registerable, LuckyBlockPlacedFeatures.SURFACE_LUCKY_BLOCKS, configured.getOrThrow(LuckyBlockConfiguredFeatures.SURFACE_LUCKY_BLOCKS),
                CountPlacementModifier.of(UniformIntProvider.create(0, 2)),
                SquarePlacementModifier.of(),
                SurfaceWaterDepthFilterPlacementModifier.of(0),
                PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP,
                BlockFilterPlacementModifier.of(BlockPredicate.solid(Direction.DOWN.getVector()))
            );
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
