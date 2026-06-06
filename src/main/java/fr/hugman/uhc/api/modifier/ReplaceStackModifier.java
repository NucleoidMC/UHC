package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;

public record ReplaceStackModifier(
        ItemPredicate predicate,
        ItemStackTemplate stack
) implements Modifier {
    public static final MapCodec<ReplaceStackModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(ReplaceStackModifier::predicate),
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ReplaceStackModifier::stack)
    ).apply(instance, ReplaceStackModifier::new));

    public static ReplaceStackModifier of(HolderGetter<Item> lookup, ItemStackTemplate stack, ItemLike... targets) {
        return new ReplaceStackModifier(ItemPredicate.Builder.item().of(lookup, targets).build(), stack);
    }

    public static ReplaceStackModifier of(HolderGetter<Item> lookup, Item replacement, ItemLike... targets) {
        return of(lookup, new ItemStackTemplate(replacement), targets);
    }

    /**
     * Enchants the given stack with the given enchantments, when it has no enchantments.
     */
    public static ReplaceStackModifier ofEnchant(HolderGetter<Item> lookup, ItemStack enchantedStack) {
        return new ReplaceStackModifier(ItemPredicate.Builder.item()
                .of(lookup, enchantedStack.getItem())
                .withComponents(DataComponentMatchers.Builder.components().exact(
                                DataComponentExactPredicate.expect(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY))
                        .build()).build(), ItemStackTemplate.fromNonEmptyStack(enchantedStack));
    }

    /**
     * Enchants the given stack with the given enchantments, when it has no enchantments.
     */
    public static ReplaceStackModifier ofEnchant(HolderGetter<Item> lookup, Item item, ItemEnchantments itemEnchantments) {
        return new ReplaceStackModifier(ItemPredicate.Builder.item()
                .of(lookup, item)
                .withComponents(DataComponentMatchers.Builder.components().exact(
                                DataComponentExactPredicate.expect(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY))
                        .build()).build(), new ItemStackTemplate(item, DataComponentPatch.builder().set(DataComponents.ENCHANTMENTS, itemEnchantments).build()));
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.REPLACE_STACK;
    }
}
