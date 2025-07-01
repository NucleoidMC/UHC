package fr.hugman.lucky_block.api.lucky_event;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import net.minecraft.registry.RegistryKey;

public class LuckyEvents {
    // Summon Entities
    public static final RegistryKey<LuckyEvent> SUMMON_BOB = of("summon_bob");
    public static final RegistryKey<LuckyEvent> SUMMON_CREEPER = of("summon_creeper");
    public static final RegistryKey<LuckyEvent> SUMMON_ONE_TNT = of("summon_one_tnt");
    public static final RegistryKey<LuckyEvent> SUMMON_ONE_WIND_CHARGE = of("summon_one_wind_charge");
    public static final RegistryKey<LuckyEvent> SUMMON_RAINBOW_SHEEP = of("summon_rainbow_sheep");

    // Loots
    public static final RegistryKey<LuckyEvent> LOOT_LUCKY_SWORD = of("loot_lucky_sword");

    private static RegistryKey<LuckyEvent> of(String path) {
        return RegistryKey.of(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockMod.id(path));
    }
}
