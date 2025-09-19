package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class UHCRegistryKeys {
    public static final RegistryKey<Registry<UHCModule>> UHC_MODULE = RegistryKey.ofRegistry(UHC.id("module"));
    public static final RegistryKey<Registry<ModifierType<?>>> MODIFIER_TYPE = RegistryKey.ofRegistry(UHC.id("modifier_type"));
    public static final RegistryKey<Registry<UHCConfig>> UHC_CONFIG = RegistryKey.ofRegistry(UHC.id("config"));
}
