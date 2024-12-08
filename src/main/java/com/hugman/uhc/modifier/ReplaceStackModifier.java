package com.hugman.uhc.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

public record ReplaceStackModifier(
        ItemStack target,
        ItemStack replacement
) implements Modifier {
    public static final MapCodec<ReplaceStackModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("target").forGetter(ReplaceStackModifier::target),
            ItemStack.VALIDATED_CODEC.fieldOf("replacement").forGetter(ReplaceStackModifier::replacement)
    ).apply(instance, ReplaceStackModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.REPLACE_STACK;
    }
}
