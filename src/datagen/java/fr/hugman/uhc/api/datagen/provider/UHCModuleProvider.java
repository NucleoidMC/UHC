package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.loot.UHCLootTables;
import fr.hugman.uhc.api.modifier.*;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCEntityTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.world.gen.feature.UHCPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UHCModuleProvider extends FabricDynamicRegistryProvider {
    public UHCModuleProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(UHCRegistryKeys.UHC_MODULE));
    }

    @Override
    public String getName() {
        return "UHC Modules";
    }

    public static void register(Registerable<UHCModule> registerable) {
        final var entities = registerable.getRegistryLookup(RegistryKeys.ENTITY_TYPE);
        final var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        final var placedFeatures = registerable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        register(registerable, UHCModules.ANIMAL_COOKED_FOOD, Items.COOKED_BEEF,
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_CHICKEN_FOOD), UHCLootTables.COOKED_CHICKEN),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_BEEF_FOOD), UHCLootTables.COOKED_BEEF_1),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_PORKCHOP_FOOD), UHCLootTables.COOKED_PORKCHOP),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MUTTON_FOOD), UHCLootTables.COOKED_MUTTON),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_RABBIT_FOOD), UHCLootTables.COOKED_RABBIT),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_FISH_FOOD), UHCLootTables.COOKED_FISH));
        register(registerable, UHCModules.BETTER_TOOLS, Items.STONE_PICKAXE,
                new ReplaceStackModifier(Items.WOODEN_SWORD, Items.STONE_SWORD),
                new ReplaceStackModifier(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE),
                new ReplaceStackModifier(Items.WOODEN_AXE, Items.STONE_AXE),
                new ReplaceStackModifier(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL),
                new ReplaceStackModifier(Items.WOODEN_HOE, Items.STONE_HOE));
        register(registerable, UHCModules.BETTER_TOOLS_PLUS, Items.IRON_PICKAXE,
                new ReplaceStackModifier(Items.STONE_SWORD, Items.IRON_SWORD),
                new ReplaceStackModifier(Items.STONE_PICKAXE, Items.IRON_PICKAXE),
                new ReplaceStackModifier(Items.STONE_AXE, Items.IRON_AXE),
                new ReplaceStackModifier(Items.STONE_SHOVEL, Items.IRON_SHOVEL),
                new ReplaceStackModifier(Items.STONE_HOE, Items.IRON_HOE),
                new ReplaceStackModifier(Items.DIAMOND_PICKAXE, ItemStackBuilder.of(Items.DIAMOND_PICKAXE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                new ReplaceStackModifier(Items.DIAMOND_AXE, ItemStackBuilder.of(Items.DIAMOND_AXE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                new ReplaceStackModifier(Items.DIAMOND_SHOVEL, ItemStackBuilder.of(Items.DIAMOND_SHOVEL).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                new ReplaceStackModifier(Items.DIAMOND_HOE, ItemStackBuilder.of(Items.DIAMOND_HOE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()));
        register(registerable, UHCModules.DASHER, Items.IRON_BOOTS,
                new PlayerAttributeModifier(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(UHC.id("dasher/movement_speed"), 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(EntityAttributes.BLOCK_BREAK_SPEED, new EntityAttributeModifier(UHC.id("dasher/block_break_speed"), 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        register(registerable, UHCModules.DASHER_PLUS, Items.DIAMOND_BOOTS,
                new PlayerAttributeModifier(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(UHC.id("dasher_plus/movement_speed"), 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(EntityAttributes.BLOCK_BREAK_SPEED, new EntityAttributeModifier(UHC.id("dasher_plus/block_break_speed"), 0.4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(EntityAttributes.SAFE_FALL_DISTANCE, new EntityAttributeModifier(UHC.id("dasher_plus/safe_fall_distance"), 1024, EntityAttributeModifier.Operation.ADD_VALUE)));
        register(registerable, UHCModules.ORE_BOOST, Items.DIAMOND_ORE,
                new PlacedFeaturesModifier(RegistryEntryList.of(
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_LAPIS_1),
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_GOLD_1),
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_DIAMOND_1)
                )));
        register(registerable, UHCModules.ORE_BOOST_PLUS, b -> b.icon(Items.DIAMOND_ORE).modifiers(
                        new PlacedFeaturesModifier(RegistryEntryList.of(
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_LAPIS_2),
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_GOLD_2),
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_DIAMOND_2)
                        )))
                .descriptionFrom(UHCModules.ORE_BOOST));
        //TODO: other modules
    }

    public static void register(
            Registerable<UHCModule> registerable,
            RegistryKey<UHCModule> key,
            ItemConvertible icon,
            Modifier... modifiers
    ) {
        register(registerable, key, b -> b.icon(icon).modifiers(modifiers));
    }

    public static void register(
            Registerable<UHCModule> registerable,
            RegistryKey<UHCModule> key,
            Function<UHCModule.Builder, UHCModule.Builder> builderFunction,
            String... longDescriptionStrings
    ) {
        var translationKey = Util.createTranslationKey("module", key.getValue());
        var builder = UHCModule.builder()
                .nameFrom(key)
                .descriptionFrom(key);

        if (longDescriptionStrings.length > 0) {
            builder.longDescription(Arrays.stream(longDescriptionStrings)
                    .map(s -> Text.translatable(translationKey + ".description." + s))
                    .collect(Collectors.toUnmodifiableList())
            );
        }

        registerable.register(key, builderFunction.apply(builder).build());
    }
}
