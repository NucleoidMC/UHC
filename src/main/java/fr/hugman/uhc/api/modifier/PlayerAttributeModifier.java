package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.impl.game.UHCPlayerManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record PlayerAttributeModifier(
        Holder<Attribute> attribute,
        AttributeModifier modifier
) implements Modifier {
    public static final MapCodec<PlayerAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(PlayerAttributeModifier::attribute),
            AttributeModifier.MAP_CODEC.forGetter(PlayerAttributeModifier::modifier)
    ).apply(instance, PlayerAttributeModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLAYER_ATTRIBUTE;
    }

    public void refreshAttribute(ServerPlayer player) {
        AttributeInstance instance = player.getAttributes().getInstance(this.attribute);
        if (instance != null) {
            instance.removeModifier(modifier);
            instance.addPermanentModifier(modifier);
        }
    }

    @Override
    public void enable(UHCPlayerManager playerManager) {
        playerManager.forEachAlive(this::refreshAttribute);
    }

    @Override
    public void disable(UHCPlayerManager playerManager) {
        playerManager.forEachAlive(player -> {
            AttributeInstance instance = player.getAttributes().getInstance(this.attribute);
            if (instance != null) {
                instance.removeModifier(modifier);
            }
        });
    }
}
