package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.datagen.compat.ULBUHCCompat;
import fr.hugman.uhc.api.loot.UHCLootTables;
import fr.hugman.uhc.api.modifier.*;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCEntityTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.util.Sprites;
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

import static fr.hugman.uhc.api.util.Sprites.all;
import static fr.hugman.uhc.api.util.Sprites.block;
import static fr.hugman.uhc.api.util.Sprites.effect;
import static fr.hugman.uhc.api.util.Sprites.grant;
import static fr.hugman.uhc.api.util.Sprites.item;
import static fr.hugman.uhc.api.util.Sprites.transformation;

public class UHCModuleProvider extends FabricDynamicRegistryProvider {
    // Ore tags vanilla ships but does not expose through BlockTags.
    private static final TagKey<Block> COAL_ORES = vanillaBlockTag("coal_ores");
    private static final TagKey<Block> LAPIS_ORES = vanillaBlockTag("lapis_ores");
    private static final TagKey<Block> REDSTONE_ORES = vanillaBlockTag("redstone_ores");
    private static final TagKey<Block> EMERALD_ORES = vanillaBlockTag("emerald_ores");
    private static final TagKey<Block> DIAMOND_ORES = vanillaBlockTag("diamond_ores");

    // Sprites for the "Need for Resources" lines, shared between the module and its + variant.
    private static final UHCModule.DescriptionLine STONES_DROP_COBBLESTONE = line("stones_drop_cobblestone",
            transformation(block("stone"), block("cobblestone")));
    private static final UHCModule.DescriptionLine CACTUS_KELP_DROP_PLANKS = line("cactus_kelp_drop_planks",
            transformation(all(block("cactus_side"), block("kelp")), block("oak_planks")));
    private static final UHCModule.DescriptionLine GRAVEL_DROPS_ARROWS_AND_FLINT_AND_STEEL = line("gravel_drops_arrows_and_flint_and_steel",
            transformation(block("gravel"), all(item("arrow"), item("flint_and_steel"))));
    private static final UHCModule.DescriptionLine SAND_DROPS_GLASS = line("sand_drops_glass",
            transformation(block("sand"), block("glass")));
    private static final UHCModule.DescriptionLine SAND_DROPS_GLASS_BOTTLES = line("sand_drops_glass_bottles",
            transformation(block("sand"), item("glass_bottle")));
    private static final UHCModule.DescriptionLine SUGAR_CANE_DROP_PAPER = line("sugar_cane_drop_paper",
            transformation(block("sugar_cane"), item("paper")));
    private static final UHCModule.DescriptionLine SUGAR_CANE_DROP_ENCHANTING_TABLES_AND_BOOKS = line("sugar_cane_drop_enchanting_tables_and_books",
            transformation(block("sugar_cane"), all(block("enchanting_table_top"), item("book"))));
    private static final UHCModule.DescriptionLine DEAD_BUSHES_DROP_BREAD = line("dead_bushes_drop_bread",
            transformation(block("dead_bush"), item("bread")));
    private static final UHCModule.DescriptionLine MUSHROOMS_DROP_STEWS = line("mushrooms_drop_stews",
            transformation(all(block("red_mushroom"), block("brown_mushroom")), item("mushroom_stew")));
    private static final UHCModule.DescriptionLine ANIMALS_AND_ZOMBIES_DROP_LEATHER = line("animals_and_zombies_drop_leather",
            transformation(all(item("cow_spawn_egg"), item("zombie_spawn_egg")), item("leather")));
    private static final UHCModule.DescriptionLine ANIMALS_AND_ZOMBIES_DROP_BOOKS = line("animals_and_zombies_drop_books",
            transformation(all(item("cow_spawn_egg"), item("zombie_spawn_egg")), item("book")));
    private static final UHCModule.DescriptionLine SHEEP_DROP_STRINGS = line("sheep_drop_strings",
            transformation(item("sheep_spawn_egg"), item("string")));
    private static final UHCModule.DescriptionLine SHEEP_DROP_BOWS = line("sheep_drop_bows",
            transformation(item("sheep_spawn_egg"), item("bow")));
    private static final UHCModule.DescriptionLine CHICKENS_DROP_ARROWS = line("chickens_drop_arrows",
            transformation(item("chicken_spawn_egg"), item("arrow")));
    private static final UHCModule.DescriptionLine CREEPERS_DROP_TNT = line("creepers_drop_tnt",
            transformation(item("creeper_spawn_egg"), block("tnt_side")));
    private static final UHCModule.DescriptionLine SQUIDS_DROP_FISHING_RODS = line("squids_drop_fishing_rods",
            transformation(item("squid_spawn_egg"), item("fishing_rod")));
    private static final UHCModule.DescriptionLine SKELETONS_DROP_POWER_BOW = line("skeletons_drop_power_bow",
            transformation(item("skeleton_spawn_egg"), all(item("bow"), item("enchanted_book"))));

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
                transformation(item("cow_spawn_egg"), item("cooked_beef")),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_CHICKEN_FOOD), UHCLootTables.COOKED_CHICKEN),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_BEEF_FOOD), UHCLootTables.COOKED_BEEF_NORMAL),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_PORKCHOP_FOOD), UHCLootTables.COOKED_PORKCHOP),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MUTTON_FOOD), UHCLootTables.COOKED_MUTTON),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_RABBIT_FOOD), UHCLootTables.COOKED_RABBIT),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_FISH_FOOD), UHCLootTables.COOKED_FISH));
        register(registerable, UHCModules.MOB_COOKED_FOOD, Items.COOKED_BEEF,
                transformation(item("zombie_spawn_egg"), item("cooked_beef")),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MOB_BEEF_FOOD), UHCLootTables.COOKED_BEEF_NORMAL),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MOB_FISH_FOOD), UHCLootTables.COOKED_FISH));
        register(registerable, UHCModules.BETTER_TOOLS, Items.STONE_PICKAXE,
                transformation(item("wooden_pickaxe"), item("stone_pickaxe")),
                ReplaceStackModifier.of(items, Items.STONE_SWORD, Items.WOODEN_SWORD),
                ReplaceStackModifier.of(items, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE),
                ReplaceStackModifier.of(items, Items.STONE_AXE, Items.WOODEN_AXE),
                ReplaceStackModifier.of(items, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL),
                ReplaceStackModifier.of(items, Items.STONE_HOE, Items.WOODEN_HOE));
        // Two unrelated changes, so they get a line each rather than one sentence carrying both.
        register(registerable, UHCModules.BETTER_TOOLS_PLUS, b -> b.icon(Items.IRON_PICKAXE).modifiers(
                        ReplaceStackModifier.of(items, Items.IRON_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD),
                        ReplaceStackModifier.of(items, Items.IRON_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE),
                        ReplaceStackModifier.of(items, Items.IRON_AXE, Items.STONE_AXE, Items.WOODEN_AXE),
                        ReplaceStackModifier.of(items, Items.IRON_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL),
                        ReplaceStackModifier.of(items, Items.IRON_HOE, Items.STONE_HOE, Items.WOODEN_HOE),
                        ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_PICKAXE, effi3),
                        ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_AXE, effi3),
                        ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_SHOVEL, effi3),
                        ReplaceStackModifier.ofEnchant(items, Items.DIAMOND_HOE, effi3))
                .longDescriptionFrom(UHCModules.BETTER_TOOLS_PLUS,
                        line("wooden_tools_become_iron", transformation(item("wooden_pickaxe"), item("iron_pickaxe"))),
                        line("diamond_tools_get_efficiency", transformation(item("diamond_pickaxe"), item("enchanted_book")))));
        register(registerable, UHCModules.DASHER, Items.IRON_BOOTS,
                grant(effect("haste"), effect("speed")),
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher/movement_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher/block_break_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        register(registerable, UHCModules.DASHER_PLUS, Items.DIAMOND_BOOTS,
                grant(effect("haste"), effect("speed"), item("feather")),
                new PlayerAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(UHC.id("dasher_plus/movement_speed"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.BLOCK_BREAK_SPEED, new AttributeModifier(UHC.id("dasher_plus/block_break_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                new PlayerAttributeModifier(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(UHC.id("dasher_plus/safe_fall_distance"), 1024, AttributeModifier.Operation.ADD_VALUE)));
        register(registerable, UHCModules.ORE_BOOST, Items.DIAMOND_ORE,
                grant(block("lapis_ore"), block("gold_ore"), block("diamond_ore")),
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
                .descriptionFrom(UHCModules.ORE_BOOST, grant(block("lapis_ore"), block("gold_ore"), block("diamond_ore"))));
        register(registerable, UHCModules.BLASTED_ORES, Items.IRON_INGOT,
                transformation(block("gold_ore"), all(item("gold_ingot"), item("experience_bottle"))),
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
                .descriptionFrom(UHCModules.BLASTED_ORES, transformation(block("gold_ore"), all(item("gold_ingot"), item("experience_bottle")))));
        register(registerable, UHCModules.GUARANTEED_APPLES, Items.APPLE,
                transformation(block("oak_leaves"), item("apple")),
                new BlockLootModifier(new TagMatchTest(BlockTags.LEAVES), UHCLootTables.APPLE));
        register(registerable, UHCModules.GUARANTEED_GOLDEN_APPLES, Items.GOLDEN_APPLE,
                transformation(block("oak_leaves"), item("golden_apple")),
                new BlockLootModifier(new TagMatchTest(BlockTags.LEAVES), UHCLootTables.GOLDEN_APPLE));
        register(registerable, UHCModules.TIMBERMAN, Items.GOLDEN_AXE,
                transformation(block("oak_log"), block("oak_planks")),
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
                STONES_DROP_COBBLESTONE,
                CACTUS_KELP_DROP_PLANKS,
                GRAVEL_DROPS_ARROWS_AND_FLINT_AND_STEEL,
                SAND_DROPS_GLASS_BOTTLES,
                SUGAR_CANE_DROP_PAPER,
                DEAD_BUSHES_DROP_BREAD,
                MUSHROOMS_DROP_STEWS,
                ANIMALS_AND_ZOMBIES_DROP_LEATHER,
                SHEEP_DROP_STRINGS,
                CHICKENS_DROP_ARROWS,
                CREEPERS_DROP_TNT));
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
                        STONES_DROP_COBBLESTONE,
                        CACTUS_KELP_DROP_PLANKS,
                        GRAVEL_DROPS_ARROWS_AND_FLINT_AND_STEEL,
                        SAND_DROPS_GLASS,
                        SUGAR_CANE_DROP_ENCHANTING_TABLES_AND_BOOKS,
                        DEAD_BUSHES_DROP_BREAD,
                        MUSHROOMS_DROP_STEWS,
                        ANIMALS_AND_ZOMBIES_DROP_BOOKS,
                        SHEEP_DROP_BOWS,
                        CHICKENS_DROP_ARROWS,
                        CREEPERS_DROP_TNT,
                        SQUIDS_DROP_FISHING_RODS,
                        SKELETONS_DROP_POWER_BOW));
        register(registerable, UHCModules.POTION_DROPS, b -> b.icon(Items.POTION).modifiers(
                new EntityLootModifier(false, entities.getOrThrow(EntityTypeTags.ZOMBIES), UHCLootTables.STRENGTH_POTIONS),
                new EntityLootModifier(false, entity(entities, EntityTypeIds.RABBIT), UHCLootTables.LEAPING_POTIONS),
                new EntityLootModifier(false, entity(entities, EntityTypeIds.BAT), UHCLootTables.NIGHT_VISION_POTIONS),
                new EntityLootModifier(false, entities.getOrThrow(UHCEntityTags.SPIDERS), UHCLootTables.POISON_POTIONS),
                new BlockLootModifier(false, new BlockMatchTest(Blocks.SUGAR_CANE), UHCLootTables.SWIFTNESS_POTIONS)
        ).longDescriptionFrom(UHCModules.POTION_DROPS,
                line("zombies_drop_strength_potions", transformation(item("zombie_spawn_egg"), effect("strength"))),
                line("spiders_drop_poison_potions", transformation(item("spider_spawn_egg"), effect("poison"))),
                line("sugar_cane_drops_swiftness_potions", transformation(block("sugar_cane"), effect("speed"))),
                line("rabbits_drop_leaping_potions", transformation(item("rabbit_spawn_egg"), effect("jump_boost"))),
                line("bats_drop_night_vision_potions", transformation(item("bat_spawn_egg"), effect("night_vision")))));

        // [COMPAT] Ultimate Lucky Block
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
            ItemLike icon,
            Sprites.Transformation sprites,
            Modifier... modifiers
    ) {
        registerable.register(key, UHCModules.create(key, icon, sprites, modifiers));
    }

    public static void register(
            BootstrapContext<UHCModule> registerable,
            ResourceKey<UHCModule> key,
            Function<UHCModule.Builder, UHCModule.Builder> builderFunction,
            String... longDescriptionStrings
    ) {
        registerable.register(key, UHCModules.create(key, builderFunction, longDescriptionStrings));
    }

    private static UHCModule.DescriptionLine line(String path, Sprites.Transformation sprites) {
        return new UHCModule.DescriptionLine(path, sprites);
    }

    private static HolderSet<EntityType<?>> entity(HolderGetter<EntityType<?>> entities, ResourceKey<EntityType<?>> key) {
        return HolderSet.direct(entities.getOrThrow(key));
    }

    private static TagKey<Block> vanillaBlockTag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(path));
    }
}
