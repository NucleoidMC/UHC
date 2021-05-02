package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;

public interface ModulePiece {
	Codec<? extends ModulePiece> getCodec();
}
