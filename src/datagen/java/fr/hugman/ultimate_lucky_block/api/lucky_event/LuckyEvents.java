package fr.hugman.ultimate_lucky_block.api.lucky_event;

import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyEvents {
    // Set Block
    public static final RegistryKey<LuckyEvent> SET_BEDROCK = of("set_bedrock");
    public static final RegistryKey<LuckyEvent> SET_BEDROCK_WORLD_PILLAR = of("set_bedrock_world_pillar");
    public static final RegistryKey<LuckyEvent> SET_RANDOM_LUCKY_BLOCK = of("set_random_lucky_block");
    public static final RegistryKey<LuckyEvent> SET_ORE_BLOCK = of("set_ore_block");
    public static final RegistryKey<LuckyEvent> SET_WOOL_PILLAR = of("set_wool_pillar");

    // Summon Entities
    public static final RegistryKey<LuckyEvent> SUMMON_TAMED_WOLF = of("summon_tamed_wolf");
    public static final RegistryKey<LuckyEvent> SUMMON_TAMED_CAT = of("summon_tamed_cat");
    public static final RegistryKey<LuckyEvent> SUMMON_HAPPY_GHAST = of("summon_happy_ghast");
    public static final RegistryKey<LuckyEvent> SUMMON_RAINBOW_SHEEP = of("summon_rainbow_sheep");

    public static final RegistryKey<LuckyEvent> SUMMON_BOB = of("summon_bob");
    public static final RegistryKey<LuckyEvent> SUMMON_ANGRY_WOLF = of("summon_angry_wolf");
    public static final RegistryKey<LuckyEvent> SUMMON_CREEPER = of("summon_creeper");
    public static final RegistryKey<LuckyEvent> SUMMON_GHAST = of("summon_ghast");
    public static final RegistryKey<LuckyEvent> SUMMON_WARDEN = of("summon_warden");
    public static final RegistryKey<LuckyEvent> SUMMON_WITHER = of("summon_wither");
    public static final RegistryKey<LuckyEvent> SUMMON_SLIME = of("summon_slime");
    public static final RegistryKey<LuckyEvent> SUMMON_WITCH = of("summon_witch");
    public static final RegistryKey<LuckyEvent> SUMMON_GIANT = of("summon_giant");
    public static final RegistryKey<LuckyEvent> SUMMON_CHARGED_CREEPER = of("summon_charged_creeper");

    public static final RegistryKey<LuckyEvent> SUMMON_ONE_TNT = of("summon_one_tnt");
    public static final RegistryKey<LuckyEvent> SUMMON_ONE_WIND_CHARGE = of("summon_one_wind_charge");

    // Loots
    public static final RegistryKey<LuckyEvent> LOOT_LUCKY_SWORD = of("loot_lucky_sword");
    public static final RegistryKey<LuckyEvent> LOOT_LUCKY_BOW = of("loot_lucky_bow");
    public static final RegistryKey<LuckyEvent> LOOT_ROTTEN_FLESH = of("loot_rotten_flesh");
    public static final RegistryKey<LuckyEvent> LOOT_ALL_DYES = of("loot_all_dyes");
    public static final RegistryKey<LuckyEvent> LOOT_END_GAME_ITEM = of("loot_end_game_item");
    public static final RegistryKey<LuckyEvent> LOOT_ELYTRA = of("loot_elytra");
    public static final RegistryKey<LuckyEvent> LOOT_BUCKETS = of("loot_buckets");
    public static final RegistryKey<LuckyEvent> LOOT_FISH_BUCKET = of("loot_fish_bucket");
    public static final RegistryKey<LuckyEvent> LOOT_EGGS = of("loot_eggs");
    public static final RegistryKey<LuckyEvent> LOOT_POTATOES = of("loot_potatoes");
    public static final RegistryKey<LuckyEvent> LOOT_PUMPKINS = of("loot_pumpkins");

    private static RegistryKey<LuckyEvent> of(String path) {
        return RegistryKey.of(ULBRegistryKeys.LUCKY_EVENT, UltimateLuckyBlock.id(path));
    }
}
