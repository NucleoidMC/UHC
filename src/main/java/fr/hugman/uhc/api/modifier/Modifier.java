package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.Codec;
import fr.hugman.uhc.api.registry.UHCRegistries;
import fr.hugman.uhc.impl.game.UHCPlayerManager;

public interface Modifier {
    Codec<Modifier> TYPE_CODEC = UHCRegistries.MODIFIER_TYPE.byNameCodec().dispatch(Modifier::getType, ModifierType::codec);

    ModifierType<?> getType();

    default void enable(UHCPlayerManager playerManager) {
    }

    default void disable(UHCPlayerManager playerManager) {
    }
}
