package fr.hugman.ultimate_lucky_block.impl.itemgroup;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import fr.hugman.ultimate_lucky_block.api.block.LuckyBlocks;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static fr.hugman.ultimate_lucky_block.api.block.LuckyBlocks.*;

public class LuckyBlockItemGroups {
    public static final ItemGroup LUCKY_BLOCKS = of("lucky_blocks", FabricItemGroup.builder()
            .displayName(Text.translatable("item_group.ultimate_lucky_block.lucky_blocks"))
            .icon(() -> new ItemStack(LuckyBlocks.LUCKY_BLOCK))
            .entries((context, entries) -> {
                entries.add(LUCKY_BLOCK);
                entries.add(SUPER_LUCKY_BLOCK);
                entries.add(VERY_LUCKY_BLOCK);
                entries.add(UNLUCKY_BLOCK);
                entries.add(VERY_UNLUCKY_BLOCK);
                entries.add(DOUBLE_LUCKY_BLOCK);
                entries.add(TRIPLE_LUCKY_BLOCK);
            })
            .build());


    private static ItemGroup of(String path, ItemGroup itemGroup) {
        PolymerItemGroupUtils.registerPolymerItemGroup(UltimateLuckyBlock.id(path), itemGroup);
        return itemGroup;
    }
}
