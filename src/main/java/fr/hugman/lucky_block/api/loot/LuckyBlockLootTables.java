package fr.hugman.lucky_block.api.loot;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class LuckyBlockLootTables {
    public static final RegistryKey<LootTable> LUCKY_SWORD = of("lucky_sword");

    private static RegistryKey<LootTable> of(String path) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, LuckyBlockMod.id(path));
    }
}
