package com.hugman.uhc.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public record PlayerAttributeModifier(
        RegistryEntry<EntityAttribute> attribute,
        EntityAttributeModifier modifier
) implements Modifier {
    public static final MapCodec<PlayerAttributeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAttribute.CODEC.fieldOf("attribute").forGetter(PlayerAttributeModifier::attribute),
            EntityAttributeModifier.MAP_CODEC.forGetter(PlayerAttributeModifier::modifier)
    ).apply(instance, PlayerAttributeModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLAYER_ATTRIBUTE;
    }

    public void refreshAttribute(ServerPlayerEntity player) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(this.attribute);
        if (instance != null) {
            instance.removeModifier(modifier);
            instance.addPersistentModifier(modifier);
        }
    }
}
