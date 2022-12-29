package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAttributeModifier implements Modifier {
	public static final Codec<PlayerAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.ATTRIBUTE.getCodec().fieldOf("attribute").forGetter(module -> module.attribute),
			Codec.doubleRange(0.0F, Double.MAX_VALUE).fieldOf("value").forGetter(module -> module.value)
	).apply(instance, PlayerAttributeModifier::new));

	private final EntityAttribute attribute;
	private final double value;

	private PlayerAttributeModifier(EntityAttribute attribute, double value) {
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierType.PLAYER_ATTRIBUTE;
	}

	public void setAttribute(ServerPlayerEntity player) {
		EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
		if (instance != null) {
			instance.setBaseValue(value);
			player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getTracked()));
		}
	}
}
