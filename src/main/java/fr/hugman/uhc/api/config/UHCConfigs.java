package fr.hugman.uhc.api.config;

import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.impl.UHC;
import net.minecraft.resources.ResourceKey;

public class UHCConfigs {
    public static final ResourceKey<UHCConfig> STANDARD_UHC = of("uhc");
    public static final ResourceKey<UHCConfig> STANDARD_UHCRUN = of("uhcrun");
    public static final ResourceKey<UHCConfig> STANDARD_DOUBLERUNNER = of("doublerunner");

    // [COMPAT] Ultimate Lucky Block
    public static final ResourceKey<UHCConfig> LUCKY_UHC = of("lucky_uhc");
    public static final ResourceKey<UHCConfig> LUCKY_UHCRUN = of("lucky_uhcrun");
    public static final ResourceKey<UHCConfig> LUCKY_DOUBLERUNNER = of("lucky_doublerunner");

    private static ResourceKey<UHCConfig> of(String path) {
        return ResourceKey.create(UHCRegistryKeys.UHC_CONFIG, UHC.id(path));
    }
}
