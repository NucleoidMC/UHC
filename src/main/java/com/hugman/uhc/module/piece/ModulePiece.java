package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import xyz.nucleoid.plasmid.game.GameLogic;

public interface ModulePiece {
	Codec<? extends ModulePiece> getCodec();

	void init(GameLogic game);
}
