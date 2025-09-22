package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.module.UHCModule;
import net.minecraft.registry.tag.TagKey;

public class UHCModuleTags {
    public static final TagKey<UHCModule> UHCRUN = of("uhcrun");
    public static final TagKey<UHCModule> DOUBLERUNNER = of("doublerunner");

    private static TagKey<UHCModule> of(String path) {
        return TagKey.of(UHCRegistryKeys.UHC_MODULE, UHC.id(path));
    }
}