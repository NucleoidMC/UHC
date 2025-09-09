package fr.hugman.ultimate_lucky_block.api.loot;

import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBLootTables {
    public static final RegistryKey<LootTable> LUCKY_SWORD = of("lucky_sword");
    public static final RegistryKey<LootTable> LUCKY_BOW = of("lucky_bow");
    public static final RegistryKey<LootTable> ALL_DYES = of("all_dyes");
    public static final RegistryKey<LootTable> END_GAME_ITEM = of("end_game_item");
    public static final RegistryKey<LootTable> ELYTRA = of("elytra");
    public static final RegistryKey<LootTable> BUCKETS = of("buckets");
    public static final RegistryKey<LootTable> FISH_BUCKET = of("fish_bucket");
    public static final RegistryKey<LootTable> ROTTEN_FLESH = of("rotten_flesh");
    public static final RegistryKey<LootTable> EGGS = of("eggs");
    public static final RegistryKey<LootTable> POTATOES = of("potatoes");
    public static final RegistryKey<LootTable> PUMPKINS = of("pumpkins");

    private static RegistryKey<LootTable> of(String path) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, UltimateLuckyBlock.id(path));
    }
}
