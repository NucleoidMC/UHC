package com.hugman.uhc;

import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.piece.ModulePieceType;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

public class UHCRegistries {
	public static final TinyRegistry<Module> MODULES = TinyRegistry.create();
	public static final TinyRegistry<ModulePieceType<?>> MODULE_PIECE_TYPES = TinyRegistry.create();
}
