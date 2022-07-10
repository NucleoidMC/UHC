package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHC;
import com.hugman.uhc.UHCRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record ModulePieceType<T extends ModulePiece>(Codec<T> codec) {
	public static final ModulePieceType<BlockLootModulePiece> BLOCK_LOOT = register("block_loot", BlockLootModulePiece.CODEC);
	public static final ModulePieceType<EntityLootModulePiece> ENTITY_LOOT = register("entity_loot", EntityLootModulePiece.CODEC);
	public static final ModulePieceType<TraversalBreakModulePiece> TRAVERSAL_BREAK = register("traversal_break", TraversalBreakModulePiece.CODEC);
	public static final ModulePieceType<PlayerAttributeModulePiece> PLAYER_ATTRIBUTE = register("player_attribute", PlayerAttributeModulePiece.CODEC);
	public static final ModulePieceType<PermanentEffectModulePiece> PERMANENT_EFFECT = register("permanent_effect", PermanentEffectModulePiece.CODEC);
	public static final ModulePieceType<PlacedFeaturesModulePiece> PLACED_FEATURES = register("placed_features", PlacedFeaturesModulePiece.CODEC);

	private static <T extends ModulePiece> ModulePieceType<T> register(String name, Codec<T> codec) {
		return register(UHC.id(name), codec);
	}

	public static <T extends ModulePiece> ModulePieceType<T> register(Identifier identifier, Codec<T> codec) {
		return Registry.register(UHCRegistries.MODULE_PIECE_TYPE, identifier, new ModulePieceType<T>(codec));
	}
}
