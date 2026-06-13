package fr.hugman.uhc.api.world.level.levelgen;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class UHCNoiseSettings {
    public static final ResourceKey<NoiseGeneratorSettings> OCEANLESS_OVERWORLD = of("oceanless_overworld");

    private static ResourceKey<NoiseGeneratorSettings> of(String path) {
        return ResourceKey.create(Registries.NOISE_SETTINGS, UHC.id(path));
    }

}
