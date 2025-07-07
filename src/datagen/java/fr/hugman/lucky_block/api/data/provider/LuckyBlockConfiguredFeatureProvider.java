package fr.hugman.lucky_block.api.data.provider;

import fr.hugman.lucky_block.api.block.LuckyBlocks;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.api.world.gen.feature.LuckyBlockConfiguredFeatures;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
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
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockConfiguredFeatureProvider extends FabricDynamicRegistryProvider {
    public LuckyBlockConfiguredFeatureProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(RegistryKeys.CONFIGURED_FEATURE);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(LuckyBlockMod.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Configured Features (Lucky)";
    }

    public static void register(Registerable<ConfiguredFeature<?, ?>> registerable) {
        var stoneOresReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        var deepslateOresReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        of(registerable, LuckyBlockConfiguredFeatures.SURFACE_LUCKY_BLOCKS, Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(LuckyBlocks.LUCKY_BLOCK)));
        of(registerable, LuckyBlockConfiguredFeatures.MINERAL_LUCKY_BLOCKS, Feature.REPLACE_SINGLE_BLOCK, new EmeraldOreFeatureConfig(List.of(
                OreFeatureConfig.createTarget(stoneOresReplaceables, LuckyBlocks.LUCKY_BLOCK.getDefaultState()), // TODO make a stone lucky block
                OreFeatureConfig.createTarget(deepslateOresReplaceables, LuckyBlocks.LUCKY_BLOCK.getDefaultState()) // TODO make a deepslate lucky block
        )));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void of(Registerable<ConfiguredFeature<?, ?>> registry, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        ConfiguredFeatures.register(registry, key, feature, config);
    }
}
