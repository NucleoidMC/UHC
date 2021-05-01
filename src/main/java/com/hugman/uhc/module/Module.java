package com.hugman.uhc.module;

import com.mojang.serialization.Codec;
import xyz.nucleoid.plasmid.game.GameLogic;

public interface Module {
	Codec<? extends Module> getCodec();

	void init(GameLogic game);
}
