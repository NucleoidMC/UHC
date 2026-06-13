package fr.hugman.uhc.api.world.level.levelgen.feature;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class UHCConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BOOSTED_LAPIS = of("boosted_ores/lapis");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BOOSTED_GOLD = of("boosted_ores/gold");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BOOSTED_DIAMOND = of("boosted_ores/diamond");

    private static ResourceKey<ConfiguredFeature<?, ?>> of(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, UHC.id(path));
    }
}
