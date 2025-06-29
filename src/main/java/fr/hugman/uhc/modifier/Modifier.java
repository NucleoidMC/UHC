package fr.hugman.uhc.modifier;

import fr.hugman.uhc.game.UHCPlayerManager;
import fr.hugman.uhc.registry.UHCRegistries;
import com.mojang.serialization.Codec;

public interface Modifier {
    Codec<Modifier> TYPE_CODEC = UHCRegistries.MODIFIER_TYPE.getCodec().dispatch(Modifier::getType, ModifierType::codec);

    ModifierType<?> getType();

    //TODO: make them fire events instead
    default void enable(UHCPlayerManager playerManager) {
    }

    default void disable(UHCPlayerManager playerManager) {
    }
}
