package fr.hugman.ultimate_lucky_block.api.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlock extends Block implements PolymerTexturedBlock, LuckyBlockInterface {
    private final BlockState model;
    private final RegistryKey<LuckyEvent> event;

    public LuckyBlock(Settings settings, RegistryKey<LuckyEvent> event, RegistryKey<Block> key) {
        super(settings);

        this.model = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(key.getValue().withPrefixedPath("block/")));
        this.event = event;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return model;
    }

    @Override
    public void onLuckyBlockTrigger(ServerWorld world, @Nullable PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        world.getRegistryManager()
                .getOrThrow(ULBRegistryKeys.LUCKY_EVENT)
                .getOrThrow(this.event).value()
                .trigger(world, player, pos, state, blockEntity);
        //TODO: add particles and sounds
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() != state.getBlock() && world instanceof ServerWorld serverWorld) {
            this.update(state, serverWorld, pos);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world instanceof ServerWorld serverWorld) {
            this.update(state, serverWorld, pos);
        }
    }

    public void update(BlockState state, ServerWorld world, BlockPos pos) {
        if (world.isReceivingRedstonePower(pos)) {
            world.removeBlock(pos, false);
            this.onLuckyBlockTrigger(world, null, pos, state, world.getBlockEntity(pos));
        }
    }
}
