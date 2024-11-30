package com.hugman.lucky_block.block;

import com.hugman.lucky_block.LuckyBlockMod;
import com.hugman.lucky_block.lucky_event.LuckyEventTags;
import com.hugman.lucky_block.lucky_event.provider.OneOfLuckyEventProvider;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class LuckyBlock extends Block implements PolymerTexturedBlock, LuckyBlockInterface {
    private final BlockState model;

    public LuckyBlock(Settings settings) {
        super(settings);

        this.model = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(LuckyBlockMod.id("block/lucky_block")));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return model;
    }

    @Override
    public void onLuckyBlockBreak(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        var provider = new OneOfLuckyEventProvider(world.getRegistryManager(), LuckyEventTags.REGULAR);

        provider.triggerEvents(world, player, pos, state, blockEntity);
    }
}
