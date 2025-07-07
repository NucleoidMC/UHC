package fr.hugman.lucky_block.api.config;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockUHCConfigs {
    public static final RegistryKey<UHCConfig> LUCKY_UHC = of("lucky_uhc");
    public static final RegistryKey<UHCConfig> LUCKY_UHCRUN = of("lucky_uhcrun");
    public static final RegistryKey<UHCConfig> LUCKY_DOUBLERUNNER = of("lucky_doublerunner");

    private static RegistryKey<UHCConfig> of(String path) {
        return RegistryKey.of(UHCRegistryKeys.UHC_CONFIG, LuckyBlockMod.id(path));
    }
}
