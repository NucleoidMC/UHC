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
        return of(key, s -> new LuckyBlock(s, event), settings, new Item.Settings().component(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT));
    }
}
