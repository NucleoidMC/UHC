package fr.hugman.lucky_block.api.module;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.RegistryKey;

public class LuckyBlockUHCModules {
    public static final RegistryKey<UHCModule> LUCKY_BLOCKS = of("lucky_blocks");

    public static RegistryKey<UHCModule> of(String path) {
        return RegistryKey.of(UHCRegistryKeys.UHC_MODULE, LuckyBlockMod.id(path));
    }
}
