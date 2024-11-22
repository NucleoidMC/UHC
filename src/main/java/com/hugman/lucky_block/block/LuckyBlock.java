package com.hugman.lucky_block.block;

import com.hugman.lucky_block.LuckyBlockMod;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public class LuckyBlock extends SimplePolymerBlock implements PolymerTexturedBlock {
    public LuckyBlock(Settings settings, Block polymerBlock) {
        super(settings, polymerBlock);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(LuckyBlockMod.id("lucky_block")));
    }
}
