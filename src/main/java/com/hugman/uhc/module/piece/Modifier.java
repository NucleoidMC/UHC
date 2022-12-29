package com.hugman.uhc.module.piece;

import com.hugman.uhc.UHCRegistries;
import com.mojang.serialization.Codec;

public interface Modifier {
	Codec<Modifier> TYPE_CODEC = UHCRegistries.MODIFIER_TYPE.getCodec().dispatch(Modifier::getType, ModifierType::codec);

	ModifierType<?> getType();
}
