package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlayerAttributesModulePiece implements ModulePiece {
	public static final Codec<PlayerAttributesModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("name").forGetter(module -> module.name),
			Codec.doubleRange(0.0F, Double.MAX_VALUE).fieldOf("value").forGetter(module -> module.value)
	).apply(instance, PlayerAttributesModulePiece::new));

	private final Identifier name;
	private final EntityAttribute attribute;
	private final double value;

	public PlayerAttributesModulePiece(Identifier name, double value) {
		this.name = name;
		this.attribute = Registry.ATTRIBUTE.getOrEmpty(name).orElseThrow(() -> new IllegalArgumentException("Can't find attribute " + name));
		this.value = value;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public void setAttribute(ServerPlayerEntity player) {
		EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
		if(instance != null) {
			instance.setBaseValue(value);
			//player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getEntityId(), player.getAttributes().getTracked()));
		}
	}
}
