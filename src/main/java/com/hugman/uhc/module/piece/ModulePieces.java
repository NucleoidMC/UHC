package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHC;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.function.Function;

public final class ModulePieces {
	private static final TinyRegistry<Codec<? extends ModulePiece>> REGISTRY = TinyRegistry.newStable();
	public static final Codec<ModulePiece> CODEC = REGISTRY.dispatchStable(ModulePiece::getCodec, Function.identity());

	public static final Identifier BLOCK_LOOT = register("block_loot", BlockLootModulePiece.CODEC);
	public static final Identifier ENTITY_LOOT = register("entity_loot", EntityLootModulePiece.CODEC);
	public static final Identifier BUCKET_BREAK = register("bucket_break", BucketBreakModulePiece.CODEC);
	public static final Identifier PLAYER_ATTRIBUTES = register("player_attribute", PlayerAttributeModulePiece.CODEC);
	public static final Identifier PERMANENT_EFFECTS = register("permanent_effect", PermanentEffectModulePiece.CODEC);
	public static final Identifier ORE = register("ore", OreModulePiece.CODEC);

	private ModulePieces() {
	}

	public static Identifier getId(ModulePiece piece) {
		return REGISTRY.getIdentifier(piece.getCodec());
	}

	private static Identifier register(String name, Codec<? extends ModulePiece> codec) {
		return register(UHC.id(name), codec);
	}

	public static Identifier register(Identifier identifier, Codec<? extends ModulePiece> codec) {
		REGISTRY.register(identifier, codec);
		return identifier;
	}
}
