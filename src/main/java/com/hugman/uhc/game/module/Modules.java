package com.hugman.uhc.game.module;

import com.hugman.uhc.UHC;
import com.mojang.serialization.Codec;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public class Modules {
	protected static final TinyRegistry<Codec<? extends Module>> REGISTRY = TinyRegistry.newStable();
	public static final Codec<Module> CODEC = REGISTRY.dispatchStable(Module::getCodec, Function.identity());

	public static void register() {
	}
}
