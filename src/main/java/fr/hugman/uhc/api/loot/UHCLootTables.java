package fr.hugman.uhc.api.loot;

import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class UHCLootTables {
    public static final RegistryKey<LootTable> COOKED_CHICKEN = of("cooked_food/chicken");
    public static final RegistryKey<LootTable> COOKED_BEEF_1 = of("cooked_food/beef/normal");
    public static final RegistryKey<LootTable> COOKED_PORKCHOP = of("cooked_food/porkchop");
    public static final RegistryKey<LootTable> COOKED_MUTTON = of("cooked_food/mutton");
    public static final RegistryKey<LootTable> COOKED_RABBIT = of("cooked_food/rabbit");
    public static final RegistryKey<LootTable> COOKED_FISH = of("cooked_food/fish");

    private static RegistryKey<LootTable> of(String path) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, UHC.id(path));
    }
}
