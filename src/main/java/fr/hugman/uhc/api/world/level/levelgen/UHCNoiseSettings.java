package fr.hugman.uhc.api.world.level.levelgen;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class UHCNoiseSettings {
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD_LOWER_SEAS = of("overworld/lower_seas");
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD_LOWER_SEAS_AMPLIFIED = of("overworld/lower_seas_amplified");
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD_LOWER_SEAS_LARGE = of("overworld/lower_seas_large");

    private static ResourceKey<NoiseGeneratorSettings> of(String path) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, UHC.id(path));
    }

}
