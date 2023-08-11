package com.hugman.uhc.registry;

import com.hugman.uhc.UHC;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public class UHCRegistries {
	public static final RegistryKey<Registry<Module>> MODULE = RegistryKey.ofRegistry(UHC.id("module"));
	public static final SimpleRegistry<ModifierType> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(ModifierType.class, UHC.id("modifier_type")).buildAndRegister();

	public static void register() {
		DynamicRegistries.register(MODULE, Module.CODEC);
	}
}
