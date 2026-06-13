package fr.hugman.uhc.api.world.level.levelgen;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;

public class UHCDensityFunctions {
    public static final ResourceKey<DensityFunction> HIGHER_DEPTH = of("overworld/depth_high");
    public static final ResourceKey<DensityFunction> HIGHER_DEPTH_AMPLIFIED = of("overworld/depth_high_amplified");
    public static final ResourceKey<DensityFunction> HIGHER_DEPTH_LARGE = of("overworld/depth_high_large");

    private static ResourceKey<DensityFunction> of(String path) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, UHC.id(path));
    }

}
