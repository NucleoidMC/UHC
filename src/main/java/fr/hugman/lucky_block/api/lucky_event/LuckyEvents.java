package fr.hugman.lucky_block.api.lucky_event;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.registry.RegistryKey;

public class LuckyEvents {
    // Pools
    public static final RegistryKey<LuckyEvent> POOL_NORMAL = of("pool/normal");

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
    public static final RegistryKey<LuckyEvent> SUMMON_WITCH = of("summon_witch");

    public static final RegistryKey<LuckyEvent> SUMMON_ONE_TNT = of("summon_one_tnt");
    public static final RegistryKey<LuckyEvent> SUMMON_ONE_WIND_CHARGE = of("summon_one_wind_charge");

    // Loots
    public static final RegistryKey<LuckyEvent> LOOT_LUCKY_SWORD = of("loot_lucky_sword");
    public static final RegistryKey<LuckyEvent> LOOT_LUCKY_BOW = of("loot_lucky_bow");
    public static final RegistryKey<LuckyEvent> LOOT_ALL_DYES = of("loot_all_dyes");
    public static final RegistryKey<LuckyEvent> LOOT_END_GAME_ITEM = of("loot_end_game_item");
    public static final RegistryKey<LuckyEvent> LOOT_SADDLE = of("loot_saddle");

    private static RegistryKey<LuckyEvent> of(String path) {
        return RegistryKey.of(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockMod.id(path));
    }
}
