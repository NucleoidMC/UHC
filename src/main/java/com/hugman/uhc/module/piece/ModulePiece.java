package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHCRegistries;
import com.mojang.serialization.Codec;

public abstract class ModulePiece {
	public static final Codec<ModulePiece> TYPE_CODEC = UHCRegistries.MODULE_PIECE_TYPES.dispatchStable(ModulePiece::getType, ModulePieceType::codec);

	public abstract ModulePieceType<?> getType();
}
