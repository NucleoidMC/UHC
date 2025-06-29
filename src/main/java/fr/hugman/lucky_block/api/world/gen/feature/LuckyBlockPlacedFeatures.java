package fr.hugman.lucky_block.api.world.gen.feature;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

public class LuckyBlockPlacedFeatures {
    public static final RegistryKey<PlacedFeature> MINERAL_LUCKY_BLOCKS = of("mineral_lucky_blocks");
    public static final RegistryKey<PlacedFeature> SURFACE_LUCKY_BLOCKS = of("surface_lucky_blocks");

    private static RegistryKey<PlacedFeature> of(String path) {
        // Temporary namespace, it will be replaced when Lucky Block becomes a standalone mod
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, UHC.id(path));
    }
}
