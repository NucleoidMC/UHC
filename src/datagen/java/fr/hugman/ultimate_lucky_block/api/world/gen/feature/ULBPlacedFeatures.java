package fr.hugman.ultimate_lucky_block.api.world.gen.feature;

import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBPlacedFeatures {
    public static final RegistryKey<PlacedFeature> MINERAL_LUCKY_BLOCKS = of("mineral_lucky_blocks");
    public static final RegistryKey<PlacedFeature> SURFACE_LUCKY_BLOCKS = of("surface_lucky_blocks");

    private static RegistryKey<PlacedFeature> of(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, UltimateLuckyBlock.id(path));
    }
}
