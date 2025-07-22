package fr.hugman.lucky_block.api.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyPoolEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlocks {
    public static final LuckyBlock LUCKY_BLOCK = luckyBlock(LuckyBlockKeys.LUCKY_BLOCK, LuckyPoolEvents.NORMAL, AbstractBlock.Settings.copy(Blocks.YELLOW_WOOL));

    public static final LuckyBlock SUPER_LUCKY_BLOCK = luckyBlock(LuckyBlockKeys.SUPER_LUCKY_BLOCK, LuckyPoolEvents.LUCKY, AbstractBlock.Settings.copy(Blocks.LIME_WOOL));
    public static final LuckyBlock VERY_LUCKY_BLOCK = luckyBlock(LuckyBlockKeys.VERY_LUCKY_BLOCK, LuckyPoolEvents.VERY_LUCKY, AbstractBlock.Settings.copy(Blocks.LIGHT_BLUE_WOOL));
    public static final LuckyBlock UNLUCKY_BLOCK = luckyBlock(LuckyBlockKeys.UNLUCKY_BLOCK, LuckyPoolEvents.UNLUCKY, AbstractBlock.Settings.copy(Blocks.RED_WOOL));
    public static final LuckyBlock VERY_UNLUCKY_BLOCK = luckyBlock(LuckyBlockKeys.VERY_UNLUCKY_BLOCK, LuckyPoolEvents.VERY_UNLUCKY, AbstractBlock.Settings.copy(Blocks.PURPLE_WOOL));

    public static final LuckyBlock DOUBLE_LUCKY_BLOCK = luckyBlock(LuckyBlockKeys.DOUBLE_LUCKY_BLOCK, LuckyPoolEvents.DOUBLE, AbstractBlock.Settings.copy(LUCKY_BLOCK));
    public static final LuckyBlock TRIPLE_LUCKY_BLOCK = luckyBlock(LuckyBlockKeys.TRIPLE_LUCKY_BLOCK, LuckyPoolEvents.TRIPLE, AbstractBlock.Settings.copy(LUCKY_BLOCK));

    private static <B extends Block & PolymerBlock> B noItem(RegistryKey<Block> key, Function<AbstractBlock.Settings, B> factory, AbstractBlock.Settings blockSettings) {
        B block = factory.apply(blockSettings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static <B extends Block & PolymerBlock> B of(RegistryKey<Block> key, Function<AbstractBlock.Settings, B> factory, AbstractBlock.Settings blockSettings, Item.Settings itemSettings) {
        B block = noItem(key, factory, blockSettings);
        var itemRegistryKey = RegistryKey.of(RegistryKeys.ITEM, key.getValue());
        Registry.register(Registries.ITEM, itemRegistryKey, new PolymerBlockItem(block, itemSettings.registryKey(itemRegistryKey).useBlockPrefixedTranslationKey()));
        return block;
    }

    private static LuckyBlock luckyBlock(RegistryKey<Block> key, RegistryKey<LuckyEvent> event, AbstractBlock.Settings settings) {
        return of(key, s -> new LuckyBlock(s, event, key), settings, new Item.Settings().component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT));
    }
}
