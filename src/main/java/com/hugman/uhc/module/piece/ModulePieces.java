package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHC;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public final class ModulePieces {
	public static final Identifier BLOCK_LOOT = UHC.id("block_loot");
	public static final Identifier ENTITY_LOOT = UHC.id("entity_loot");
	public static final Identifier BUCKET_BREAK = UHC.id("bucket_break");
	private static final TinyRegistry<Codec<? extends ModulePiece>> REGISTRY = TinyRegistry.newStable();
	public static final Codec<ModulePiece> CODEC = REGISTRY.dispatchStable(ModulePiece::getCodec, Function.identity());

	public static void register() {
		register(BLOCK_LOOT, BlockLootModulePiece.CODEC);
		register(ENTITY_LOOT, EntityLootModulePiece.CODEC);
		register(BUCKET_BREAK, BucketBreakModulePiece.CODEC);
	}

	public static Identifier getId(ModulePiece piece) {
		return REGISTRY.getIdentifier(piece.getCodec());
	}

	private static void register(Identifier identifier, Codec<? extends ModulePiece> codec) {
		REGISTRY.register(identifier, codec);
	}
}
