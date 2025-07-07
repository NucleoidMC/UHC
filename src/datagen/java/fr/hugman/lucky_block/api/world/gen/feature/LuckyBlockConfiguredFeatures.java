package fr.hugman.lucky_block.api.world.gen.feature;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> MINERAL_LUCKY_BLOCKS = of("mineral_lucky_blocks");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SURFACE_LUCKY_BLOCKS = of("surface_lucky_blocks");

    private static RegistryKey<ConfiguredFeature<?, ?>> of(String path) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, LuckyBlockMod.id(path));
    }
}
