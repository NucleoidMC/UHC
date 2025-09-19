package fr.hugman.uhc.api.world.gen.feature;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class UHCConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> BOOSTED_LAPIS = of("boosted_ores/lapis");
    public static final RegistryKey<ConfiguredFeature<?, ?>> BOOSTED_GOLD = of("boosted_ores/gold");
    public static final RegistryKey<ConfiguredFeature<?, ?>> BOOSTED_DIAMOND = of("boosted_ores/diamond");

    private static RegistryKey<ConfiguredFeature<?, ?>> of(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, UHC.id(path));
    }
}
