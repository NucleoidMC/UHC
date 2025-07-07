package fr.hugman.lucky_block.api.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlock extends Block implements PolymerTexturedBlock, LuckyBlockInterface {
    private final BlockState model;
    private final RegistryKey<LuckyEvent> event;

    public LuckyBlock(Settings settings, RegistryKey<LuckyEvent> event) {
        super(settings);

        this.model = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(LuckyBlockMod.id("block/lucky_block")));
        this.event = event;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return model;
    }

    @Override
    public void onLuckyBlockBreak(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        world.getRegistryManager()
                .getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT)
                .getOrThrow(this.event).value()
                .trigger(world, player, pos, state, blockEntity);
    }
}
