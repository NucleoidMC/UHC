package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;

public record ReplaceStackModifier(
        ItemPredicate predicate,
        ItemStack stack
) implements Modifier {
    public static final MapCodec<ReplaceStackModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPredicate.CODEC.fieldOf("predicate").forGetter(ReplaceStackModifier::predicate),
            ItemStack.VALIDATED_CODEC.fieldOf("stack").forGetter(ReplaceStackModifier::stack)
    ).apply(instance, ReplaceStackModifier::new));

    public static ReplaceStackModifier of(RegistryEntryLookup<Item> lookup, ItemStack stack, ItemConvertible... targets) {
        return new ReplaceStackModifier(ItemPredicate.Builder.create().items(lookup, targets).build(), stack);
    }

    public static ReplaceStackModifier of(RegistryEntryLookup<Item> lookup, Item replacement, ItemConvertible... targets) {
        return of(lookup, replacement.getDefaultStack(), targets);
    }

    /**
     * Enchants the given stack with the given enchantments, when it has no enchantments.
     */
    public static ReplaceStackModifier ofEnchant(RegistryEntryLookup<Item> lookup, ItemStack enchantedStack) {
        return new ReplaceStackModifier(ItemPredicate.Builder.create()
                .items(lookup, enchantedStack.getItem())
                .components(ComponentsPredicate.Builder.create().exact(
                                ComponentMapPredicate.of(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT))
                        .build()).build(), enchantedStack);
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.REPLACE_STACK;
    }
}
