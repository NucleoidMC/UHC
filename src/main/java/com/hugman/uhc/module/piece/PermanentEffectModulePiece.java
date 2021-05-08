package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class PermanentEffectModulePiece implements ModulePiece {
	public static final Codec<PermanentEffectModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.STATUS_EFFECT.fieldOf("effect").forGetter(module -> module.effect),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amplifier", 0).forGetter(module -> module.amplifier)
	).apply(instance, PermanentEffectModulePiece::new));

	private final StatusEffect effect;
	private final int amplifier;

	public PermanentEffectModulePiece(StatusEffect effect, int amplifier) {
		this.effect = effect;
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
