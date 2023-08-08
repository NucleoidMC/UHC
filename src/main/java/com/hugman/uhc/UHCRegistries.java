package com.hugman.uhc;

import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

public class UHCRegistries {
	public static final TinyRegistry<Module> MODULE = TinyRegistry.create();
	public static final SimpleRegistry<ModifierType> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(ModifierType.class, UHC.id("modifier_type")).buildAndRegister();
}