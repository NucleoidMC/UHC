package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

public record PermanentEffectModifier(StatusEffect effect, int amplifier) implements Modifier {
	public static final Codec<PermanentEffectModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.STATUS_EFFECT.getCodec().fieldOf("effect").forGetter(module -> module.effect),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amplifier", 0).forGetter(module -> module.amplifier)
	).apply(instance, PermanentEffectModifier::new));

	@Override
	public ModifierType<?> getType() {
		return ModifierType.PERMANENT_EFFECT;
	}

	public void setEffect(ServerPlayerEntity player) {
		player.addStatusEffect(new StatusEffectInstance(effect, -1, amplifier, false, false, true));
	}
}
