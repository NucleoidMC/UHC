package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public record ReplaceStackModifier(
        ItemStack target, //TODO: use a predicate
        ItemStack replacement
) implements Modifier {
    public static final MapCodec<ReplaceStackModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("target").forGetter(ReplaceStackModifier::target),
            ItemStack.VALIDATED_CODEC.fieldOf("replacement").forGetter(ReplaceStackModifier::replacement)
    ).apply(instance, ReplaceStackModifier::new));

    public ReplaceStackModifier(Item target, Item replacement) {
        this(target.getDefaultStack(), replacement.getDefaultStack());
    }

    public ReplaceStackModifier(Item target, ItemStack replacement) {
        this(target.getDefaultStack(), replacement);
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.REPLACE_STACK;
    }
}
