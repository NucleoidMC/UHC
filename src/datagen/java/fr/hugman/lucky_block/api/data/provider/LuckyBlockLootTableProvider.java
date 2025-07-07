package fr.hugman.lucky_block.api.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static fr.hugman.lucky_block.api.loot.LuckyBlockLootTables.*;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockLootTableProvider extends SimpleFabricLootTableProvider {
    public LuckyBlockLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.BLOCK);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer = (lootTableRegistryKey, builder) -> builder.randomSequenceId(lootTableRegistryKey.getValue());
        consumer = consumer.andThen(lootTableBiConsumer);

        consumer.accept(LUCKY_SWORD, LootTable.builder().pool(LootPool.builder().with(
                ItemEntry.builder(Items.GOLDEN_SWORD)
                        .apply(EnchantRandomlyLootFunction.create())
                        .apply(SetNameLootFunction.builder(Text.of("Lucky Sword"), SetNameLootFunction.Target.CUSTOM_NAME)))
        ));
        consumer.accept(LUCKY_BOW, LootTable.builder().pool(LootPool.builder().with(
                ItemEntry.builder(Items.BOW)
                        .apply(EnchantRandomlyLootFunction.create())
                        .apply(SetNameLootFunction.builder(Text.of("Lucky Bow"), SetNameLootFunction.Target.CUSTOM_NAME)))
        ));
        var allDyes = LootTable.builder();
        for (DyeColor color : DyeColor.values()) {
            allDyes.pool(LootPool.builder().with(ItemEntry.builder(DyeItem.byColor(color))).build());
        }
        consumer.accept(ALL_DYES, allDyes);
        consumer.accept(END_GAME_ITEM, LootTable.builder().pool(LootPool.builder()
                .with(ItemEntry.builder(Items.NETHER_STAR))
                .with(ItemEntry.builder(Items.BEACON))
                .with(ItemEntry.builder(Items.DRAGON_EGG))
                .with(ItemEntry.builder(Items.CONDUIT))
                .build()));
        consumer.accept(SADDLE, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(Items.SADDLE))));
    }
}
