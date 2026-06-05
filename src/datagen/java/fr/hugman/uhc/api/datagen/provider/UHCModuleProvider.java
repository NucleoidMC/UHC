package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.loot.UHCLootTables;
import fr.hugman.uhc.api.modifier.*;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCEntityTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.world.gen.feature.UHCPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import xyz.nucleoid.plasmid.api.util.ItemStackBuilder;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UHCModuleProvider extends FabricDynamicRegistryProvider {
    public UHCModuleProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(UHCRegistryKeys.UHC_MODULE));
    }

    @Override
    public String getName() {
        return "UHC Modules";
    }

    public static void register(BootstrapContext<UHCModule> registerable) {
        final var items = registerable.lookup(Registries.ITEM);
        final var entities = registerable.lookup(Registries.ENTITY_TYPE);
        final var enchantments = registerable.lookup(Registries.ENCHANTMENT);
        final var placedFeatures = registerable.lookup(Registries.PLACED_FEATURE);

        register(registerable, UHCModules.ANIMAL_COOKED_FOOD, Items.COOKED_BEEF,
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_CHICKEN_FOOD), UHCLootTables.COOKED_CHICKEN),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_BEEF_FOOD), UHCLootTables.COOKED_BEEF_1),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_PORKCHOP_FOOD), UHCLootTables.COOKED_PORKCHOP),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MUTTON_FOOD), UHCLootTables.COOKED_MUTTON),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_RABBIT_FOOD), UHCLootTables.COOKED_RABBIT),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_FISH_FOOD), UHCLootTables.COOKED_FISH));
        register(registerable, UHCModules.BETTER_TOOLS, Items.STONE_PICKAXE,
                ReplaceStackModifier.of(items, Items.STONE_SWORD, Items.WOODEN_SWORD),
                ReplaceStackModifier.of(items, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE),
                ReplaceStackModifier.of(items, Items.STONE_AXE, Items.WOODEN_AXE),
                ReplaceStackModifier.of(items, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL),
                ReplaceStackModifier.of(items, Items.STONE_HOE, Items.WOODEN_HOE));
        register(registerable, UHCModules.BETTER_TOOLS_PLUS, Items.IRON_PICKAXE,
                ReplaceStackModifier.of(items, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD),
                ReplaceStackModifier.of(items, Items.IRON_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE),
                ReplaceStackModifier.of(items, Items.IRON_AXE, Items.STONE_AXE, Items.WOODEN_AXE),
                ReplaceStackModifier.of(items, Items.IRON_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL),
                ReplaceStackModifier.of(items, Items.IRON_HOE, Items.STONE_HOE, Items.WOODEN_HOE),
                ReplaceStackModifier.ofEnchant(items, ItemStackBuilder.of(Items.DIAMOND_PICKAXE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                ReplaceStackModifier.ofEnchant(items, ItemStackBuilder.of(Items.DIAMOND_AXE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                ReplaceStackModifier.ofEnchant(items, ItemStackBuilder.of(Items.DIAMOND_SHOVEL).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()),
                ReplaceStackModifier.ofEnchant(items, ItemStackBuilder.of(Items.DIAMOND_HOE).addEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3).build()));
        register(registerable, UHCModules.DASHER, Items.IRON_BOOTS,
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher/movement_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher/block_break_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        register(registerable, UHCModules.DASHER_PLUS, Items.DIAMOND_BOOTS,
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher_plus/movement_speed"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher_plus/block_break_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(UHC.id("dasher_plus/safe_fall_distance"), 1024, AttributeModifier.Operation.ADD_VALUE)));
        register(registerable, UHCModules.ORE_BOOST, Items.DIAMOND_ORE,
                new PlacedFeaturesModifier(HolderSet.direct(
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_LAPIS_1),
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_GOLD_1),
                        placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_DIAMOND_1)
                )));
        register(registerable, UHCModules.ORE_BOOST_PLUS, b -> b.icon(Items.DIAMOND_ORE).modifiers(
                        new PlacedFeaturesModifier(HolderSet.direct(
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_LAPIS_2),
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_GOLD_2),
                                placedFeatures.getOrThrow(UHCPlacedFeatures.BOOSTED_DIAMOND_2)
                        )))
                .descriptionFrom(UHCModules.ORE_BOOST));
        //TODO: other modules
    }

    public static void register(
            BootstrapContext<UHCModule> registerable,
            ResourceKey<UHCModule> key,
            ItemLike icon,
            Modifier... modifiers
    ) {
        registerable.register(key, UHCModules.create(key, icon, modifiers));
    }

    public static void register(
            BootstrapContext<UHCModule> registerable,
            ResourceKey<UHCModule> key,
            Function<UHCModule.Builder, UHCModule.Builder> builderFunction,
            String... longDescriptionStrings
    ) {
        registerable.register(key, UHCModules.create(key, builderFunction, longDescriptionStrings));
    }
}
