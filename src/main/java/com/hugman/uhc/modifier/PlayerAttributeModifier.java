package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerAttributeModifier(RegistryEntry<EntityAttribute> attribute, double value) implements Modifier {
    public static final MapCodec<PlayerAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(PlayerAttributeModifier::attribute),
            Codec.doubleRange(0.0F, Double.MAX_VALUE).fieldOf("value").forGetter(PlayerAttributeModifier::value)
    ).apply(instance, PlayerAttributeModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLAYER_ATTRIBUTE;
    }

    public void setAttribute(ServerPlayerEntity player) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(this.attribute);
        if (instance != null) {
            instance.setBaseValue(value);
            player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getTracked()));
        }
    }
}
