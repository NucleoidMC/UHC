package fr.hugman.ultimate_lucky_block.api.module;

import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBUHCModules {
    public static final RegistryKey<UHCModule> LUCKY_BLOCKS = of("lucky_blocks");

    public static RegistryKey<UHCModule> of(String path) {
        return RegistryKey.of(UHCRegistryKeys.UHC_MODULE, UltimateLuckyBlock.id(path));
    }
}
