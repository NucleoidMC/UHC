package fr.hugman.uhc.api.world.gen.feature;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class UHCPlacedFeatures {
    public static final ResourceKey<PlacedFeature> BOOSTED_LAPIS_1 = of("boosted_ores_1/lapis");
    public static final ResourceKey<PlacedFeature> BOOSTED_GOLD_1 = of("boosted_ores_1/gold");
    public static final ResourceKey<PlacedFeature> BOOSTED_DIAMOND_1 = of("boosted_ores_1/diamond");

    public static final ResourceKey<PlacedFeature> BOOSTED_LAPIS_2 = of("boosted_ores_2/lapis");
    public static final ResourceKey<PlacedFeature> BOOSTED_GOLD_2 = of("boosted_ores_2/gold");
    public static final ResourceKey<PlacedFeature> BOOSTED_DIAMOND_2 = of("boosted_ores_2/diamond");

    private static ResourceKey<PlacedFeature> of(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, UHC.id(path));
    }
}
