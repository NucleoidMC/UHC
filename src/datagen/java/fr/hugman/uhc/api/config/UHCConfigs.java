package fr.hugman.uhc.api.config;

import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.RegistryKey;

public class UHCConfigs {
    public static final RegistryKey<UHCConfig> STANDARD_UHC = of("uhc");
    public static final RegistryKey<UHCConfig> STANDARD_UHCRUN = of("uhcrun");
    public static final RegistryKey<UHCConfig> STANDARD_DOUBLERUNNER = of("doublerunner");

    private static RegistryKey<UHCConfig> of(String path) {
        return RegistryKey.of(UHCRegistryKeys.UHC_CONFIG, UHC.id(path));
    }
}
