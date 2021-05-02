package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHC;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public final class ModulePieces {
	public static final Identifier LOOT_REPLACE = UHC.id("loot_replace");
	public static final Identifier BUCKET_BREAK = UHC.id("bucket_break");
	private static final TinyRegistry<Codec<? extends ModulePiece>> REGISTRY = TinyRegistry.newStable();
	public static final Codec<ModulePiece> CODEC = REGISTRY.dispatchStable(ModulePiece::getCodec, Function.identity());

	public static void register() {
		register(LOOT_REPLACE, LootReplaceModulePiece.CODEC);
		register(BUCKET_BREAK, BucketBreakModulePiece.CODEC);
	}

	public static Identifier getId(ModulePiece piece) {
		return REGISTRY.getIdentifier(piece.getCodec());
	}

	private static void register(Identifier identifier, Codec<? extends ModulePiece> codec) {
		REGISTRY.register(identifier, codec);
	}
}
