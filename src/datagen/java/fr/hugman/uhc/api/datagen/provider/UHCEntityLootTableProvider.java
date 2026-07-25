package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.loot.UHCLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class UHCEntityLootTableProvider extends UHCLootTableProvider {
    public UHCEntityLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        // Animal & mob cooked food
        exporter.accept(UHCLootTables.COOKED_CHICKEN, single(uniform(Items.COOKED_CHICKEN, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.COOKED_BEEF_SOME, single(uniform(Items.COOKED_BEEF, 1.0F, 2.0F)));
        exporter.accept(UHCLootTables.COOKED_BEEF_NORMAL, single(uniform(Items.COOKED_BEEF, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.COOKED_BEEF_LOT, single(uniform(Items.COOKED_BEEF, 3.0F, 4.0F)));
        exporter.accept(UHCLootTables.COOKED_PORKCHOP, single(uniform(Items.COOKED_PORKCHOP, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.COOKED_MUTTON, single(uniform(Items.COOKED_MUTTON, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.COOKED_RABBIT, single(uniform(Items.COOKED_RABBIT, 1.0F, 3.0F)));
        exporter.accept(UHCLootTables.COOKED_FISH, single(uniform(Items.COOKED_COD, 1.0F, 2.0F)));

        // Faster resources
        exporter.accept(UHCLootTables.ARROWS_NORMAL, single(uniform(Items.ARROW, 3.0F, 4.0F)));
        exporter.accept(UHCLootTables.ARROWS_BUNCH, single(uniform(Items.ARROW, 7.0F, 9.0F)));
        exporter.accept(UHCLootTables.ARROWS_LOT, single(uniform(Items.ARROW, 14.0F, 18.0F)));
        exporter.accept(UHCLootTables.LEATHER_NORMAL, single(uniform(Items.LEATHER, 0.0F, 2.0F)));
        exporter.accept(UHCLootTables.LEATHER_LOT, single(uniform(Items.LEATHER, 1.0F, 3.0F)));
        exporter.accept(UHCLootTables.TNTS_NORMAL, single(uniform(Items.TNT, 1.0F, 2.0F)));
        exporter.accept(UHCLootTables.TNTS_LOT, single(uniform(Items.TNT, 2.0F, 3.0F)));
        exporter.accept(UHCLootTables.BOOK, single(uniform(Items.BOOK, 0.0F, 1.0F)
                .apply(looting())));
        exporter.accept(UHCLootTables.STRINGS, single(uniform(Items.STRING, 1.0F, 2.0F)
                .apply(looting())));
        exporter.accept(UHCLootTables.BOW, single(LootItem.lootTableItem(Items.BOW)
                .apply(looting())
                .when(LootItemRandomChanceCondition.randomChance(0.2F))));
        exporter.accept(UHCLootTables.POWER_BOW, single(LootItem.lootTableItem(Items.BOW)
                .apply(SetComponentsFunction.setComponent(DataComponents.ENCHANTMENTS, power()))
                .apply(looting())
                .when(LootItemRandomChanceCondition.randomChance(0.2F))));
        exporter.accept(UHCLootTables.FISHING_ROD, single(LootItem.lootTableItem(Items.FISHING_ROD)
                .when(LootItemRandomChanceCondition.randomChance(0.3F))));

        // Potion drops
        exporter.accept(UHCLootTables.STRENGTH_POTIONS, single(potion(new PotionContents(Potions.STRENGTH))
                .when(LootItemRandomChanceCondition.randomChance(0.15F))));
        exporter.accept(UHCLootTables.LEAPING_POTIONS, single(potion(new PotionContents(Potions.LEAPING))
                .when(LootItemRandomChanceCondition.randomChance(0.3F))));
        exporter.accept(UHCLootTables.NIGHT_VISION_POTIONS, single(potion(new PotionContents(Potions.NIGHT_VISION))));
        exporter.accept(UHCLootTables.POISON_POTIONS, single(potion(new PotionContents(
                Optional.empty(),
                Optional.empty(),
                List.of(new MobEffectInstance(MobEffects.POISON, 200, 0)),
                Optional.empty()))
                .when(LootItemRandomChanceCondition.randomChance(0.3F))));
    }

    private static LootPoolSingletonContainer.Builder<?> uniform(ItemLike item, float min, float max) {
        return LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
    }

    private static LootPoolSingletonContainer.Builder<?> potion(PotionContents contents) {
        return LootItem.lootTableItem(Items.POTION)
                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, contents));
    }

    private LootItemFunction.Builder looting() {
        return EnchantedCountIncreaseFunction.lootingMultiplier(registries(), UniformGenerator.between(0.0F, 1.0F));
    }

    private ItemEnchantments power() {
        var enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(enchantment(Enchantments.POWER), 1);
        return enchantments.toImmutable();
    }
}
