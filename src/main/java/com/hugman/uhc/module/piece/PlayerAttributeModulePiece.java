package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class PlayerAttributeModulePiece extends ModulePiece {
	public static final Codec<PlayerAttributeModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.ATTRIBUTE.getCodec().fieldOf("attribute").forGetter(module -> module.attribute),
			Codec.doubleRange(0.0F, Double.MAX_VALUE).fieldOf("value").forGetter(module -> module.value)
	).apply(instance, PlayerAttributeModulePiece::new));

	private final EntityAttribute attribute;
	private final double value;

	private PlayerAttributeModulePiece(EntityAttribute attribute, double value) {
		this.attribute = attribute;
		this.value = value;
	}

	@Override
	public ModulePieceType<?> getType() {
		return ModulePieceType.PLAYER_ATTRIBUTE;
	}

	public void setAttribute(ServerPlayerEntity player) {
		EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
		if(instance != null) {
			instance.setBaseValue(value);
			player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getTracked()));
		}
	}
}
