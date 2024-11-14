package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public record PermanentEffectModifier(RegistryEntry<StatusEffect> effect, int amplifier) implements Modifier {
	public static final MapCodec<PermanentEffectModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			StatusEffect.ENTRY_CODEC.fieldOf("effect").forGetter(PermanentEffectModifier::effect),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amplifier", 0).forGetter(PermanentEffectModifier::amplifier)
	).apply(instance, PermanentEffectModifier::new));

	@Override
	public ModifierType<?> getType() {
		return ModifierType.PERMANENT_EFFECT;
	}

	public void setEffect(ServerPlayerEntity player) {
		player.addStatusEffect(new StatusEffectInstance(this.effect, -1, this.amplifier, false, false, true));
	}
}
