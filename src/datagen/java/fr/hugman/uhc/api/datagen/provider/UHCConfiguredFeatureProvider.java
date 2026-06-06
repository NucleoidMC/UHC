package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.world.gen.feature.UHCConfiguredFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UHCConfiguredFeatureProvider extends FabricDynamicRegistryProvider {
    public UHCConfiguredFeatureProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
    }

    @Override
    public String getName() {
        return "Configured Features";
    }

    public static void register(BootstrapContext<ConfiguredFeature<?, ?>> registerable) {
        var stoneOresReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var deepslateOresReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // Boosted Ores
        of(registerable, UHCConfiguredFeatures.BOOSTED_LAPIS, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(stoneOresReplaceables, Blocks.LAPIS_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOresReplaceables, Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState())
        ), 13));
        of(registerable, UHCConfiguredFeatures.BOOSTED_GOLD, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(stoneOresReplaceables, Blocks.GOLD_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOresReplaceables, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState())
        ), 12));
        of(registerable, UHCConfiguredFeatures.BOOSTED_DIAMOND, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(stoneOresReplaceables, Blocks.DIAMOND_ORE.defaultBlockState()),
                OreConfiguration.target(deepslateOresReplaceables, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState())
        ), 7));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void of(BootstrapContext<ConfiguredFeature<?, ?>> registry, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        FeatureUtils.register(registry, key, feature, config);
    }
}
