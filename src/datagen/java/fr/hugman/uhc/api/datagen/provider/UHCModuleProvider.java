package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.datagen.compat.ULBUHCCompat;
import fr.hugman.uhc.api.loot.UHCLootTables;
import fr.hugman.uhc.api.modifier.*;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCEntityTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.world.level.levelgen.feature.UHCPlacedFeatures;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.ultimate_lucky_block.api.block.ULBBlocks;
import fr.hugman.ultimate_lucky_block.api.world.gen.feature.ULBPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class UHCModuleProvider extends FabricDynamicRegistryProvider {
    // Ore tags vanilla ships but does not expose through BlockTags.
    private static final TagKey<Block> COAL_ORES = vanillaBlockTag("coal_ores");
    private static final TagKey<Block> LAPIS_ORES = vanillaBlockTag("lapis_ores");
    private static final TagKey<Block> REDSTONE_ORES = vanillaBlockTag("redstone_ores");
    private static final TagKey<Block> EMERALD_ORES = vanillaBlockTag("emerald_ores");
    private static final TagKey<Block> DIAMOND_ORES = vanillaBlockTag("diamond_ores");

    public UHCModuleProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        ULBUHCCompat.addAll(entries, registries.lookupOrThrow(UHCRegistryKeys.UHC_MODULE));
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

        var effi3Builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        effi3Builder.set(enchantments.getOrThrow(Enchantments.EFFICIENCY), 3);
        var effi3 = effi3Builder.toImmutable();

        register(registerable, UHCModules.ANIMAL_COOKED_FOOD, Items.COOKED_BEEF,
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_CHICKEN_FOOD), UHCLootTables.COOKED_CHICKEN),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_BEEF_FOOD), UHCLootTables.COOKED_BEEF_NORMAL),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_PORKCHOP_FOOD), UHCLootTables.COOKED_PORKCHOP),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MUTTON_FOOD), UHCLootTables.COOKED_MUTTON),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_RABBIT_FOOD), UHCLootTables.COOKED_RABBIT),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_FISH_FOOD), UHCLootTables.COOKED_FISH));
        register(registerable, UHCModules.MOB_COOKED_FOOD, Items.COOKED_BEEF,
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MOB_BEEF_FOOD), UHCLootTables.COOKED_BEEF_NORMAL),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MOB_FISH_FOOD), UHCLootTables.COOKED_FISH));
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
                ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_PICKAXE, effi3),
                ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_AXE, effi3),
                ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_SHOVEL, effi3),
                ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_HOE, effi3));
        register(registerable, UHCModules.DASHER, Items.IRON_BOOTS,
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher/movement_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher/block_break_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        register(registerable, UHCModules.DASHER_PLUS, Items.DIAMOND_BOOTS,
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher_plus/movement_speed"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher_plus/block_break_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(UHC.id("dasher_plus/safe_fall_distance"), 1024, AttributeModifier.Operation.ADD_VALUE)));
        register(registerable, UHCModules.ORE_BOOST, Items.DIAMOND_ORE,
                new PlacedFeaturesModifier(HolderSet.direct(placedFeatures::getOrThrow,
                        UHCPlacedFeatures.BOOSTED_LAPIS_1,
                        UHCPlacedFeatures.BOOSTED_GOLD_1,
                        UHCPlacedFeatures.BOOSTED_DIAMOND_1
                )));
        register(registerable, UHCModules.ORE_BOOST_PLUS, b -> b.icon(Items.DIAMOND_ORE).modifiers(
                        new PlacedFeaturesModifier(HolderSet.direct(placedFeatures::getOrThrow,
                                UHCPlacedFeatures.BOOSTED_LAPIS_2,
                                UHCPlacedFeatures.BOOSTED_GOLD_2,
                                UHCPlacedFeatures.BOOSTED_DIAMOND_2
                        )))
                .descriptionFrom(UHCModules.ORE_BOOST));
        register(registerable, UHCModules.BLASTED_ORES, Items.IRON_INGOT,
                new BlockLootModifier(new TagMatchTest(COAL_ORES), UHCLootTables.TORCHES_NORMAL, 4),
                new BlockLootModifier(new TagMatchTest(BlockTags.IRON_ORES), UHCLootTables.IRON_INGOTS_TWO, 1),
                new BlockLootModifier(new TagMatchTest(BlockTags.COPPER_ORES), UHCLootTables.COOKED_BEEF_SOME),
                new BlockLootModifier(new TagMatchTest(BlockTags.GOLD_ORES), UHCLootTables.GOLD_INGOTS_TWO, 2),
                new BlockLootModifier(new TagMatchTest(LAPIS_ORES), UHCLootTables.LAPIS_AND_PAPER, 6),
                new BlockLootModifier(new TagMatchTest(REDSTONE_ORES), 8),
                new BlockLootModifier(new TagMatchTest(EMERALD_ORES), 12),
                new BlockLootModifier(new TagMatchTest(DIAMOND_ORES), UHCLootTables.DIAMONDS_TWO, 5));
        register(registerable, UHCModules.BLASTED_ORES_PLUS, b -> b.icon(Items.GOLD_INGOT).modifiers(
                        new BlockLootModifier(new TagMatchTest(COAL_ORES), UHCLootTables.TORCHES_LOT, 6),
                        new BlockLootModifier(new TagMatchTest(BlockTags.IRON_ORES), UHCLootTables.IRON_INGOTS_FOUR, 2),
                        new BlockLootModifier(new TagMatchTest(BlockTags.COPPER_ORES), UHCLootTables.COOKED_BEEF_NORMAL),
                        new BlockLootModifier(new TagMatchTest(BlockTags.GOLD_ORES), UHCLootTables.GOLD_INGOTS_FOUR, 4),
                        new BlockLootModifier(new TagMatchTest(LAPIS_ORES), UHCLootTables.LAPIS_AND_PAPER, 8),
                        new BlockLootModifier(new TagMatchTest(REDSTONE_ORES), 12),
                        new BlockLootModifier(new TagMatchTest(EMERALD_ORES), 20),
                        new BlockLootModifier(new TagMatchTest(DIAMOND_ORES), UHCLootTables.DIAMONDS_FOUR, 8))
                .descriptionFrom(UHCModules.BLASTED_ORES));
        register(registerable, UHCModules.GUARANTEED_APPLES, Items.APPLE,
                new BlockLootModifier(new TagMatchTest(BlockTags.LEAVES), UHCLootTables.APPLE));
        register(registerable, UHCModules.GUARANTEED_GOLDEN_APPLES, Items.GOLDEN_APPLE,
                new BlockLootModifier(new TagMatchTest(BlockTags.LEAVES), UHCLootTables.GOLDEN_APPLE));
        register(registerable, UHCModules.TIMBERMAN, Items.GOLDEN_AXE,
                new TraversalBreakModifier(new TagMatchTest(BlockTags.LOGS), true),
                new BlockLootModifier(new TagMatchTest(BlockTags.LOGS), UHCLootTables.OAK_PLANKS_NORMAL));
        register(registerable, UHCModules.FASTER_RESOURCES, b -> b.icon(Items.CACTUS).modifiers(
                new BlockLootModifier(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), UHCLootTables.COBBLESTONE),
                new BlockLootModifier(new TagMatchTest(BlockTags.SAND), UHCLootTables.GLASS),
                new BlockLootModifier(new BlockMatchTest(Blocks.GRAVEL), UHCLootTables.ARROWS_NORMAL),
                new BlockLootModifier(new BlockMatchTest(Blocks.CACTUS), UHCLootTables.OAK_PLANKS_SOME),
                new BlockLootModifier(new BlockMatchTest(Blocks.KELP), UHCLootTables.OAK_PLANKS_SOME),
                new BlockLootModifier(new BlockMatchTest(Blocks.KELP_PLANT), UHCLootTables.OAK_PLANKS_SOME),
                new BlockLootModifier(new BlockMatchTest(Blocks.BAMBOO), UHCLootTables.OAK_PLANKS_SOME),
                new BlockLootModifier(new BlockMatchTest(Blocks.DEAD_BUSH), UHCLootTables.BREAD_NORMAL),
                new BlockLootModifier(new BlockMatchTest(Blocks.RED_MUSHROOM), UHCLootTables.MUSHROOM_STEWS),
                new BlockLootModifier(new BlockMatchTest(Blocks.BROWN_MUSHROOM), UHCLootTables.MUSHROOM_STEWS),
                new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.DROPS_LEATHER), UHCLootTables.LEATHER_NORMAL),
                new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.DROPS_STRING), UHCLootTables.STRINGS),
                new BlockLootModifier(new BlockMatchTest(Blocks.GRAVEL), UHCLootTables.FLINT_AND_STEEL),
                new BlockLootModifier(new BlockMatchTest(Blocks.SUGAR_CANE), UHCLootTables.PAPER),
                new EntityLootModifier(false, entity(entities, EntityTypeIds.CHICKEN), UHCLootTables.ARROWS_NORMAL),
                new EntityLootModifier(entity(entities, EntityTypeIds.CREEPER), UHCLootTables.TNTS_NORMAL)
        ).longDescriptionFrom(UHCModules.FASTER_RESOURCES,
                "stones_drop_cobblestone",
                "cactus_kelp_drop_planks",
                "gravel_drops_arrows_and_flint_and_steel",
                "sand_drops_glass_bottles",
                "sugar_cane_drop_paper",
                "dead_bushes_drop_bread",
                "mushrooms_drop_stews",
                "animals_and_zombies_drop_leather",
                "sheep_drop_strings",
                "chickens_drop_arrows",
                "creepers_drop_tnt"));
        register(registerable, UHCModules.FASTER_RESOURCES_PLUS, b -> b.icon(Items.CACTUS).modifiers(
                        new BlockLootModifier(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), UHCLootTables.COBBLESTONE),
                        new BlockLootModifier(new TagMatchTest(BlockTags.SAND), UHCLootTables.GLASS_BOTTLES),
                        new BlockLootModifier(new BlockMatchTest(Blocks.GRAVEL), UHCLootTables.ARROWS_LOT),
                        new BlockLootModifier(new BlockMatchTest(Blocks.CACTUS), UHCLootTables.OAK_PLANKS_SOME),
                        new BlockLootModifier(new BlockMatchTest(Blocks.KELP), UHCLootTables.OAK_PLANKS_SOME),
                        new BlockLootModifier(new BlockMatchTest(Blocks.KELP_PLANT), UHCLootTables.OAK_PLANKS_SOME),
                        new BlockLootModifier(new BlockMatchTest(Blocks.BAMBOO), UHCLootTables.OAK_PLANKS_SOME),
                        new BlockLootModifier(new BlockMatchTest(Blocks.DEAD_BUSH), UHCLootTables.BREAD_NORMAL),
                        new BlockLootModifier(new BlockMatchTest(Blocks.RED_MUSHROOM), UHCLootTables.MUSHROOM_STEWS),
                        new BlockLootModifier(new BlockMatchTest(Blocks.BROWN_MUSHROOM), UHCLootTables.MUSHROOM_STEWS),
                        new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.DROPS_LEATHER), UHCLootTables.BOOK),
                        new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.SQUIDS), UHCLootTables.FISHING_ROD),
                        new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.DROPS_STRING), UHCLootTables.BOW),
                        new BlockLootModifier(new BlockMatchTest(Blocks.GRAVEL), UHCLootTables.FLINT_AND_STEEL),
                        new EntityLootModifier(false, entity(entities, EntityTypeIds.CHICKEN), UHCLootTables.ARROWS_BUNCH),
                        new EntityLootModifier(entities.getOrThrow(EntityTypeTags.SKELETONS), UHCLootTables.POWER_BOW),
                        new BlockLootModifier(new BlockMatchTest(Blocks.SUGAR_CANE), UHCLootTables.TABLE_OR_BOOKS),
                        new EntityLootModifier(entity(entities, EntityTypeIds.CREEPER), UHCLootTables.TNTS_NORMAL))
                .descriptionFrom(UHCModules.FASTER_RESOURCES)
                .longDescriptionFrom(UHCModules.FASTER_RESOURCES,
                        "stones_drop_cobblestone",
                        "cactus_kelp_drop_planks",
                        "gravel_drops_arrows_and_flint_and_steel",
                        "sand_drops_glass",
                        "sugar_cane_drop_enchanting_tables_and_books",
                        "dead_bushes_drop_bread",
                        "mushrooms_drop_stews",
                        "animals_and_zombies_drop_books",
                        "sheep_drop_bows",
                        "chickens_drop_arrows",
                        "creepers_drop_tnt",
                        "squids_drop_fishing_rods",
                        "skeletons_drop_power_bow"));
        register(registerable, UHCModules.POTION_DROPS, b -> b.icon(Items.POTION).modifiers(
                new EntityLootModifier(false, entities.getOrThrow(EntityTypeTags.ZOMBIES), UHCLootTables.STRENGTH_POTIONS),
                new EntityLootModifier(false, entity(entities, EntityTypeIds.RABBIT), UHCLootTables.LEAPING_POTIONS),
                new EntityLootModifier(false, entity(entities, EntityTypeIds.BAT), UHCLootTables.NIGHT_VISION_POTIONS),
                new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.SPIDERS), UHCLootTables.POISON_POTIONS),
                new BlockLootModifier(false, new BlockMatchTest(Blocks.SUGAR_CANE), UHCLootTables.SWIFTNESS_POTIONS)
        ).longDescriptionFrom(UHCModules.POTION_DROPS,
                "zombies_drop_strength_potions",
                "spiders_drop_poison_potions",
                "sugar_cane_drops_swiftness_potions",
                "rabbits_drop_leaping_potions",
                "bats_drop_night_vision_potions"));

        register(registerable, UHCModules.LUCKY_BLOCKS, ULBBlocks.LUCKY_BLOCK,
                new PlacedFeaturesModifier(HolderSet.direct(placedFeatures::getOrThrow,
                        ULBPlacedFeatures.SURFACE_LUCKY_BLOCKS,
                        ULBPlacedFeatures.MINERAL_LUCKY_BLOCKS
                ))
        );
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

    private static HolderSet<EntityType<?>> entity(HolderGetter<EntityType<?>> entities, ResourceKey<EntityType<?>> key) {
        return HolderSet.direct(entities.getOrThrow(key));
    }

    private static TagKey<Block> vanillaBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(path));
    }
}
