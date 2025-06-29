package fr.hugman.uhc.registry;

import fr.hugman.uhc.UHC;
import fr.hugman.uhc.config.UHCConfig;
import fr.hugman.uhc.modifier.ModifierType;
import fr.hugman.uhc.module.Module;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class UHCRegistryKeys {
    public static final RegistryKey<Registry<Module>> MODULE = RegistryKey.ofRegistry(UHC.id("module"));
    public static final RegistryKey<Registry<ModifierType<?>>> MODIFIER_TYPE = RegistryKey.ofRegistry(UHC.id("modifier_type"));
    public static final RegistryKey<Registry<UHCConfig>> UHC_CONFIG = RegistryKey.ofRegistry(UHC.id("config"));

    public static void registerDynamics() {
        DynamicRegistries.register(MODULE, Module.CODEC);
        DynamicRegistries.register(UHC_CONFIG, UHCConfig.CODEC);
    }
}
