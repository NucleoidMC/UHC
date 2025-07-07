package fr.hugman.lucky_block.api.loot;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockLootTables {
    public static final RegistryKey<LootTable> LUCKY_SWORD = of("lucky_sword");
    public static final RegistryKey<LootTable> LUCKY_BOW = of("lucky_bow");
    public static final RegistryKey<LootTable> ALL_DYES = of("all_dyes");
    public static final RegistryKey<LootTable> END_GAME_ITEM = of("end_game_item");
    public static final RegistryKey<LootTable> SADDLE = of("saddle");

    private static RegistryKey<LootTable> of(String path) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, LuckyBlockMod.id(path));
    }
}
