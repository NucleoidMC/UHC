package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHC;
import com.mojang.serialization.Codec;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public class ModulePieces {
	private static final TinyRegistry<Codec<? extends ModulePiece>> REGISTRY = TinyRegistry.newStable();
	public static final Codec<ModulePiece> CODEC = REGISTRY.dispatchStable(ModulePiece::getCodec, Function.identity());

	public static void register() {
		register("loot_replace", LootReplaceModulePiece.CODEC);
		register("bucket_break", BucketBreakModulePiece.CODEC);
	}

	private static void register(String identifier, Codec<? extends ModulePiece> codec) {
		REGISTRY.register(UHC.id(identifier), codec);
	}
}
