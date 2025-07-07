package fr.hugman.lucky_block.api.registry;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class LuckyBlockUHCModuleTags {
    public static final TagKey<UHCModule> UHCRUN = of("uhcrun");
    public static final TagKey<UHCModule> DOUBLERUNNER = of("doublerunner");

    private static TagKey<UHCModule> of(String path) {
        return TagKey.of(UHCRegistryKeys.UHC_MODULE, LuckyBlockMod.id(path));
    }
}