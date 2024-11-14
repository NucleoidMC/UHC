package com.hugman.uhc.registry;

import com.hugman.uhc.UHC;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class UHCRegistryKeys {
    public static final RegistryKey<Registry<Module>> MODULE = RegistryKey.ofRegistry(UHC.id("module"));
    public static final RegistryKey<Registry<ModifierType<?>>> MODIFIER_TYPE = RegistryKey.ofRegistry(UHC.id("modifier_type"));

    public static void registerDynamics() {
        DynamicRegistries.register(MODULE, Module.CODEC);
    }
}
