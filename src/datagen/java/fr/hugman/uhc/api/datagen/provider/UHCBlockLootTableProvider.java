package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.loot.UHCLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class UHCBlockLootTableProvider extends UHCLootTableProvider {
    public UHCBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, LootContextParamSets.BLOCK);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        var fortune = enchantment(Enchantments.FORTUNE);

        // Guaranteed (golden) apples
        exporter.accept(UHCLootTables.APPLE, single(LootItem.lootTableItem(Items.APPLE)
                .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, 0.01F, 0.03F, 0.06F, 0.09F, 0.12F))));
        exporter.accept(UHCLootTables.GOLDEN_APPLE, single(LootItem.lootTableItem(Items.GOLDEN_APPLE)
                .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, 0.01F, 0.03F, 0.06F, 0.09F, 0.12F))));

        // Faster resources
        exporter.accept(UHCLootTables.COBBLESTONE, single(LootItem.lootTableItem(Items.COBBLESTONE)));
        exporter.accept(UHCLootTables.GLASS, single(LootItem.lootTableItem(Items.GLASS)));
        exporter.accept(UHCLootTables.GLASS_BOTTLES, single(uniform(Items.GLASS_BOTTLE, 1.0F, 2.0F)));
        exporter.accept(UHCLootTables.BREAD_NORMAL, single(uniform(Items.BREAD, 1.0F, 2.0F)));
        exporter.accept(UHCLootTables.BREAD_LOT, single(uniform(Items.BREAD, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.MUSHROOM_STEWS, single(LootItem.lootTableItem(Items.MUSHROOM_STEW)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))));
        exporter.accept(UHCLootTables.OAK_PLANKS_SOME, single(uniform(Items.OAK_PLANKS, 1.0F, 2.0F)));
        exporter.accept(UHCLootTables.OAK_PLANKS_NORMAL, single(uniform(Items.OAK_PLANKS, 3.0F, 4.0F)));
        exporter.accept(UHCLootTables.OAK_PLANKS_LOT, single(uniform(Items.OAK_PLANKS, 4.0F, 6.0F)));
        exporter.accept(UHCLootTables.FLINT_AND_STEEL, single(LootItem.lootTableItem(Items.FLINT_AND_STEEL)
                .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F))));

        // Blasted ores
        exporter.accept(UHCLootTables.TORCHES_NORMAL, single(ore(Items.TORCH, 3)));
        exporter.accept(UHCLootTables.TORCHES_LOT, single(ore(Items.TORCH, 8)));
        exporter.accept(UHCLootTables.IRON_INGOTS_TWO, single(ore(Items.IRON_INGOT, 2)));
        exporter.accept(UHCLootTables.IRON_INGOTS_FOUR, single(ore(Items.IRON_INGOT, 4)));
        exporter.accept(UHCLootTables.GOLD_INGOTS_TWO, single(ore(Items.GOLD_INGOT, 2)));
        exporter.accept(UHCLootTables.GOLD_INGOTS_FOUR, single(ore(Items.GOLD_INGOT, 3)));
        exporter.accept(UHCLootTables.DIAMONDS_TWO, single(ore(Items.DIAMOND, 2)));
        exporter.accept(UHCLootTables.DIAMONDS_FOUR, single(ore(Items.DIAMOND, 4)));

        // Enchantment resources
        exporter.accept(UHCLootTables.LAPIS_AND_PAPER, LootTable.lootTable().withPool(pool(2,
                LootItem.lootTableItem(Items.LAPIS_LAZULI)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 4.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(fortune))
                        .apply(ApplyExplosionDecay.explosionDecay()),
                LootItem.lootTableItem(Items.PAPER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.5F)))
                        .apply(ApplyBonusCount.addOreBonusCount(fortune))
                        .apply(ApplyExplosionDecay.explosionDecay()))));
        exporter.accept(UHCLootTables.PAPER, single(LootItem.lootTableItem(Items.PAPER)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                .apply(ApplyBonusCount.addOreBonusCount(fortune))
                .apply(ApplyExplosionDecay.explosionDecay())));
        exporter.accept(UHCLootTables.TABLE_OR_BOOKS, LootTable.lootTable()
                .withPool(pool(1, LootItem.lootTableItem(Items.BOOK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addOreBonusCount(fortune))
                        .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(pool(1, LootItem.lootTableItem(Items.ENCHANTING_TABLE)
                        .apply(ApplyBonusCount.addOreBonusCount(fortune))
                        .apply(ApplyExplosionDecay.explosionDecay())
                        .when(LootItemRandomChanceCondition.randomChance(0.05F)))));

        // Potion drops
        exporter.accept(UHCLootTables.SWIFTNESS_POTIONS, single(LootItem.lootTableItem(Items.POTION)
                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SWIFTNESS)))
                .when(LootItemRandomChanceCondition.randomChance(0.15F))));
    }

    private static LootPoolSingletonContainer.Builder<?> uniform(ItemLike item, float min, float max) {
        return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
    }

    private LootPoolSingletonContainer.Builder<?> ore(ItemLike item, int count) {
        return LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                .apply(ApplyBonusCount.addOreBonusCount(enchantment(Enchantments.FORTUNE)));
    }
}
