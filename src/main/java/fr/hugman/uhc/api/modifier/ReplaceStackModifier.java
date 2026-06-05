package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;

public record ReplaceStackModifier(
        ItemPredicate predicate,
        ItemStack stack
) implements Modifier {
    public static final MapCodec<ReplaceStackModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(ReplaceStackModifier::predicate),
            ItemStack.STRICT_CODEC.fieldOf("stack").forGetter(ReplaceStackModifier::stack)
    ).apply(instance, ReplaceStackModifier::new));

    public static ReplaceStackModifier of(HolderGetter<Item> lookup, ItemStack stack, ItemLike... targets) {
        return new ReplaceStackModifier(ItemPredicate.Builder.item().of(lookup, targets).build(), stack);
    }

    public static ReplaceStackModifier of(HolderGetter<Item> lookup, Item replacement, ItemLike... targets) {
        return of(lookup, replacement.getDefaultInstance(), targets);
    }

    /**
     * Enchants the given stack with the given enchantments, when it has no enchantments.
     */
    public static ReplaceStackModifier ofEnchant(HolderGetter<Item> lookup, ItemStack enchantedStack) {
        return new ReplaceStackModifier(ItemPredicate.Builder.item()
                .of(lookup, enchantedStack.getItem())
                .withComponents(DataComponentMatchers.Builder.components().exact(
                                DataComponentExactPredicate.expect(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY))
                        .build()).build(), enchantedStack);
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.REPLACE_STACK;
    }
}
