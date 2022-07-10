package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHCRegistries;
import com.mojang.serialization.Codec;

public interface ModulePiece {
	Codec<ModulePiece> TYPE_CODEC = UHCRegistries.MODULE_PIECE_TYPE.getCodec().dispatch(ModulePiece::getType, ModulePieceType::codec);

	ModulePieceType<?> getType();
}
