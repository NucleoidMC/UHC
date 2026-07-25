package fr.hugman.uhc.api.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

/**
 * Base class for the UHC loot table providers, sharing helpers for the single-pool tables that make up most of them.
 */
public abstract class UHCLootTableProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    protected UHCLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ContextKeySet contextParamSet) {
        super(output, registriesFuture, contextParamSet);
        this.registriesFuture = registriesFuture;
    }

    /**
     * The registries are always available by the time {@link #generate} runs, as the provider is only run once the
     * future has completed.
     */
    protected HolderLookup.Provider registries() {
        return this.registriesFuture.join();
    }

    protected Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
        return this.registries().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    /**
     * Creates a table made of a single pool rolled once, containing the given entries.
     */
    protected static LootTable.Builder single(LootPoolSingletonContainer.Builder<?>... entries) {
        return LootTable.lootTable().withPool(pool(1, entries));
    }

    /**
     * Creates a pool rolled the given amount of times, containing the given entries.
     */
    protected static LootPool.Builder pool(int rolls, LootPoolSingletonContainer.Builder<?>... entries) {
        var pool = LootPool.lootPool().setRolls(ConstantValue.exactly(rolls));
        for (var entry : entries) {
            pool.add(entry);
        }
        return pool;
    }
}
