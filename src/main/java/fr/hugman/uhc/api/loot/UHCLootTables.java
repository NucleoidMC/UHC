package fr.hugman.uhc.api.loot;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class UHCLootTables {
    public static final ResourceKey<LootTable> COOKED_CHICKEN = of("cooked_food/chicken");
    public static final ResourceKey<LootTable> COOKED_BEEF_1 = of("cooked_food/beef/normal");
    public static final ResourceKey<LootTable> COOKED_PORKCHOP = of("cooked_food/porkchop");
    public static final ResourceKey<LootTable> COOKED_MUTTON = of("cooked_food/mutton");
    public static final ResourceKey<LootTable> COOKED_RABBIT = of("cooked_food/rabbit");
    public static final ResourceKey<LootTable> COOKED_FISH = of("cooked_food/fish");

    private static ResourceKey<LootTable> of(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, UHC.id(path));
    }
}
