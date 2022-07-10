package com.hugman.uhc;

import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.piece.ModulePieceType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.SimpleRegistry;

public class UHCRegistries {
	public static final SimpleRegistry<Module> MODULE = FabricRegistryBuilder.createSimple(Module.class, UHC.id("uhc/module")).buildAndRegister();
	public static final SimpleRegistry<ModulePieceType> MODULE_PIECE_TYPE = FabricRegistryBuilder.createSimple(ModulePieceType.class, UHC.id("uhc/module_type")).buildAndRegister();
}
