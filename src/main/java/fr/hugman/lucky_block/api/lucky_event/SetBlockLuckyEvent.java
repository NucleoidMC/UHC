package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event that sets a block in the world.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record SetBlockLuckyEvent(
        BlockStateProvider stateProvider,
        Vec3i offset
) implements LuckyEvent {
    public static final Vec3i DEFAULT_OFFSET = Vec3i.ZERO;

    public static final MapCodec<SetBlockLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(SetBlockLuckyEvent::stateProvider),
            Vec3i.CODEC.optionalFieldOf("offset", DEFAULT_OFFSET).forGetter(SetBlockLuckyEvent::offset)
    ).apply(instance, SetBlockLuckyEvent::new));

    public SetBlockLuckyEvent(BlockStateProvider provider) {
        this(provider, DEFAULT_OFFSET);
    }

    public SetBlockLuckyEvent(BlockState state) {
        this(BlockStateProvider.of(state));
    }

    public SetBlockLuckyEvent(Block block) {
        this(BlockStateProvider.of(block));
    }

    public SetBlockLuckyEvent(Block... blocks) {
        this(getListProvider(blocks), DEFAULT_OFFSET);
    }

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        world.setBlockState(pos.add(offset), state);
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.SET_BLOCK;
    }

    private static WeightedBlockStateProvider getListProvider(Block... blocks) {
        var pool = new Pool.Builder<BlockState>();
        for (Block block : blocks) {
            pool.add(block.getDefaultState());
        }
        return new WeightedBlockStateProvider(pool);
    }
}
