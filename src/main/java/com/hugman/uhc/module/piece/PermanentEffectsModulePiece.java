package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PermanentEffectsModulePiece implements ModulePiece {
	public static final Codec<PermanentEffectsModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("name").forGetter(module -> module.name),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amplifier", 0).forGetter(module -> module.amplifier)
	).apply(instance, PermanentEffectsModulePiece::new));

	private final Identifier name;
	private final StatusEffect effect;
	private final int amplifier;

	public PermanentEffectsModulePiece(Identifier name, int amplifier) {
		this.name = name;
		this.effect = Registry.STATUS_EFFECT.getOrEmpty(name).orElseThrow(() -> new IllegalArgumentException("Can't find status effect " + name));
		this.amplifier = amplifier;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public void setEffect(ServerPlayerEntity player, int effectDuration) {
		player.addStatusEffect(new StatusEffectInstance(effect, effectDuration, amplifier, false, false, true));
	}
}
