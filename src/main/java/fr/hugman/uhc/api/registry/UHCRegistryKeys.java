package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class UHCRegistryKeys {
    public static final ResourceKey<Registry<UHCModule>> UHC_MODULE = ResourceKey.createRegistryKey(UHC.id("module"));
    public static final ResourceKey<Registry<ModifierType<?>>> MODIFIER_TYPE = ResourceKey.createRegistryKey(UHC.id("modifier_type"));
    public static final ResourceKey<Registry<UHCConfig>> UHC_CONFIG = ResourceKey.createRegistryKey(UHC.id("config"));
}
