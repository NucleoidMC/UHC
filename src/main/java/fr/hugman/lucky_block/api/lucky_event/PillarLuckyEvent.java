package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event that sets a pillar of blocks in the world (from bottom to top of the world).
 *
 * @author Hugman
 * @since 1.0.0
 */
public record PillarLuckyEvent(
        BlockStateProvider stateProvider,
        boolean top,
        boolean bottom
) implements LuckyEvent {
    public static final boolean DEFAULT_TOP = true;
    public static final boolean DEFAULT_BOTTOM = false;

    public static final MapCodec<PillarLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(PillarLuckyEvent::stateProvider),
            Codec.BOOL.optionalFieldOf("top",DEFAULT_TOP).forGetter(PillarLuckyEvent::top),
            Codec.BOOL.optionalFieldOf("bottom", DEFAULT_BOTTOM).forGetter(PillarLuckyEvent::bottom)
    ).apply(instance, PillarLuckyEvent::new));

    public PillarLuckyEvent(BlockState state) {
        this(BlockStateProvider.of(state), DEFAULT_TOP, DEFAULT_BOTTOM);
    }

    public PillarLuckyEvent(BlockState state, boolean top, boolean bottom) {
        this(BlockStateProvider.of(state), top, bottom);
    }

    public PillarLuckyEvent(Block block, boolean top, boolean bottom) {
        this(BlockStateProvider.of(block), top, bottom);
    }

    public PillarLuckyEvent(Block block) {
        this(BlockStateProvider.of(block), DEFAULT_TOP, DEFAULT_BOTTOM);
    }

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        world.setBlockState(pos, stateProvider.get(world.getRandom(), pos), Block.NOTIFY_ALL);
        if (top) {
            for (int y = pos.getY(); y < world.getHeight(); y++) {
                world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), stateProvider.get(world.getRandom(), pos), Block.NOTIFY_ALL);
            }
        }
        if (bottom) {
            for (int y = pos.getY(); y >= world.getBottomY(); y--) {
                world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), stateProvider.get(world.getRandom(), pos), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.WORLD_PILLAR;
    }
}
