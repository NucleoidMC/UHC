package fr.hugman.uhc.api.loot;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class UHCLootTables {
    // Blocks
    public static final ResourceKey<LootTable> APPLE = of("apple");
    public static final ResourceKey<LootTable> GOLDEN_APPLE = of("golden_apple");
    public static final ResourceKey<LootTable> BREAD_NORMAL = of("bread/normal");
    public static final ResourceKey<LootTable> BREAD_LOT = of("bread/lot");
    public static final ResourceKey<LootTable> COBBLESTONE = of("cobblestone");
    public static final ResourceKey<LootTable> GLASS = of("glass");
    public static final ResourceKey<LootTable> GLASS_BOTTLES = of("glass_bottles");
    public static final ResourceKey<LootTable> FLINT_AND_STEEL = of("flint_and_steel");
    public static final ResourceKey<LootTable> MUSHROOM_STEWS = of("mushroom_stews");
    public static final ResourceKey<LootTable> OAK_PLANKS_SOME = of("oak_planks/some");
    public static final ResourceKey<LootTable> OAK_PLANKS_NORMAL = of("oak_planks/normal");
    public static final ResourceKey<LootTable> OAK_PLANKS_LOT = of("oak_planks/lot");
    public static final ResourceKey<LootTable> TORCHES_NORMAL = of("torches/normal");
    public static final ResourceKey<LootTable> TORCHES_LOT = of("torches/lot");
    public static final ResourceKey<LootTable> IRON_INGOTS_TWO = of("iron_ingots/two");
    public static final ResourceKey<LootTable> IRON_INGOTS_FOUR = of("iron_ingots/four");
    public static final ResourceKey<LootTable> GOLD_INGOTS_TWO = of("gold_ingots/two");
    public static final ResourceKey<LootTable> GOLD_INGOTS_FOUR = of("gold_ingots/four");
    public static final ResourceKey<LootTable> DIAMONDS_TWO = of("diamonds/two");
    public static final ResourceKey<LootTable> DIAMONDS_FOUR = of("diamonds/four");
    public static final ResourceKey<LootTable> LAPIS_AND_PAPER = of("enchantment_resources/lapis_and_paper");
    public static final ResourceKey<LootTable> PAPER = of("enchantment_resources/paper");
    public static final ResourceKey<LootTable> TABLE_OR_BOOKS = of("enchantment_resources/table_or_books");
    public static final ResourceKey<LootTable> SWIFTNESS_POTIONS = of("potion_drops/swiftness");

    // Entities
    public static final ResourceKey<LootTable> ARROWS_NORMAL = of("arrows/normal");
    public static final ResourceKey<LootTable> ARROWS_BUNCH = of("arrows/bunch");
    public static final ResourceKey<LootTable> ARROWS_LOT = of("arrows/lot");
    public static final ResourceKey<LootTable> BOOK = of("book");
    public static final ResourceKey<LootTable> BOW = of("bow");
    public static final ResourceKey<LootTable> POWER_BOW = of("power_bow");
    public static final ResourceKey<LootTable> FISHING_ROD = of("fishing_rod");
    public static final ResourceKey<LootTable> STRINGS = of("strings");
    public static final ResourceKey<LootTable> LEATHER_NORMAL = of("leather/normal");
    public static final ResourceKey<LootTable> LEATHER_LOT = of("leather/lot");
    public static final ResourceKey<LootTable> TNTS_NORMAL = of("tnts/normal");
    public static final ResourceKey<LootTable> TNTS_LOT = of("tnts/lot");
    public static final ResourceKey<LootTable> COOKED_CHICKEN = of("cooked_food/chicken");
    public static final ResourceKey<LootTable> COOKED_BEEF_SOME = of("cooked_food/beef/some");
    public static final ResourceKey<LootTable> COOKED_BEEF_NORMAL = of("cooked_food/beef/normal");
    public static final ResourceKey<LootTable> COOKED_BEEF_LOT = of("cooked_food/beef/lot");
    public static final ResourceKey<LootTable> COOKED_PORKCHOP = of("cooked_food/porkchop");
    public static final ResourceKey<LootTable> COOKED_MUTTON = of("cooked_food/mutton");
    public static final ResourceKey<LootTable> COOKED_RABBIT = of("cooked_food/rabbit");
    public static final ResourceKey<LootTable> COOKED_FISH = of("cooked_food/fish");
    public static final ResourceKey<LootTable> LEAPING_POTIONS = of("potion_drops/leaping");
    public static final ResourceKey<LootTable> NIGHT_VISION_POTIONS = of("potion_drops/night_vision");
    public static final ResourceKey<LootTable> POISON_POTIONS = of("potion_drops/poison");
    public static final ResourceKey<LootTable> STRENGTH_POTIONS = of("potion_drops/strength");

    private static ResourceKey<LootTable> of(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, UHC.id(path));
    }
}
