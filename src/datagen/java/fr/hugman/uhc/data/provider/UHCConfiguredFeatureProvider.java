package fr.hugman.uhc.data.provider;

import fr.hugman.uhc.impl.world.gen.feature.UHCConfiguredFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UHCConfiguredFeatureProvider extends FabricDynamicRegistryProvider {
    public UHCConfiguredFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.CONFIGURED_FEATURE));
    }

    @Override
    public String getName() {
        return "Configured Features";
    }

    public static void register(Registerable<ConfiguredFeature<?, ?>> registerable) {
        var stoneOresReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        var deepslateOresReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // Boosted Ores
        of(registerable, UHCConfiguredFeatures.BOOSTED_LAPIS, Feature.ORE, new OreFeatureConfig(List.of(
                OreFeatureConfig.createTarget(stoneOresReplaceables, Blocks.LAPIS_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOresReplaceables, Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState())
        ), 13));
        of(registerable, UHCConfiguredFeatures.BOOSTED_GOLD, Feature.ORE, new OreFeatureConfig(List.of(
                OreFeatureConfig.createTarget(stoneOresReplaceables, Blocks.GOLD_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOresReplaceables, Blocks.DEEPSLATE_GOLD_ORE.getDefaultState())
        ), 12));
        of(registerable, UHCConfiguredFeatures.BOOSTED_DIAMOND, Feature.ORE, new OreFeatureConfig(List.of(
                OreFeatureConfig.createTarget(stoneOresReplaceables, Blocks.DIAMOND_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(deepslateOresReplaceables, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
        ), 7));
    }


    private static <FC extends FeatureConfig, F extends Feature<FC>> void of(Registerable<ConfiguredFeature<?, ?>> registry, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        ConfiguredFeatures.register(registry, key, feature, config);
    }
}
