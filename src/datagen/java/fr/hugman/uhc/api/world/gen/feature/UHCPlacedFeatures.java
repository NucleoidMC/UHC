package fr.hugman.uhc.api.world.gen.feature;

import fr.hugman.uhc.UHC;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.PlacedFeature;

public class UHCPlacedFeatures {
    public static final RegistryKey<PlacedFeature> BOOSTED_LAPIS_1 = of("boosted_ores_1/lapis");
    public static final RegistryKey<PlacedFeature> BOOSTED_GOLD_1 = of("boosted_ores_1/gold");
    public static final RegistryKey<PlacedFeature> BOOSTED_DIAMOND_1 = of("boosted_ores_1/diamond");

    public static final RegistryKey<PlacedFeature> BOOSTED_LAPIS_2 = of("boosted_ores_2/lapis");
    public static final RegistryKey<PlacedFeature> BOOSTED_GOLD_2 = of("boosted_ores_2/gold");
    public static final RegistryKey<PlacedFeature> BOOSTED_DIAMOND_2 = of("boosted_ores_2/diamond");

    private static RegistryKey<PlacedFeature> of(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, UHC.id(path));
    }
}
