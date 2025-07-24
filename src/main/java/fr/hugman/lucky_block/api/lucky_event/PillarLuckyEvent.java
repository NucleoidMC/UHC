package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
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
        HeightProvider top,
        HeightProvider bottom
) implements LuckyEvent {
    public static final ConstantHeightProvider DEFAULT_TOP = ConstantHeightProvider.create(YOffset.TOP);
    public static final ConstantHeightProvider DEFAULT_BOTTOM = ConstantHeightProvider.create(YOffset.fixed(0));

    public static final MapCodec<PillarLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.TYPE_CODEC.fieldOf("state").forGetter(PillarLuckyEvent::stateProvider),
            HeightProvider.CODEC.optionalFieldOf("top", DEFAULT_TOP).forGetter(PillarLuckyEvent::top),
            HeightProvider.CODEC.optionalFieldOf("bottom", DEFAULT_BOTTOM).forGetter(PillarLuckyEvent::bottom)
    ).apply(instance, PillarLuckyEvent::new));

    public PillarLuckyEvent(BlockStateProvider provider, boolean top, boolean bottom) {
        this(provider,
                top ? ConstantHeightProvider.create(YOffset.TOP) : ConstantHeightProvider.create(YOffset.fixed(0)),
                bottom ? ConstantHeightProvider.create(YOffset.BOTTOM) : ConstantHeightProvider.create(YOffset.fixed(0))
        );
    }

    public PillarLuckyEvent(BlockState state, boolean top, boolean bottom) {
        this(BlockStateProvider.of(state), top, bottom);
    }

    public PillarLuckyEvent(Block block, boolean top, boolean bottom) {
        this(BlockStateProvider.of(block), top, bottom);
    }

    public PillarLuckyEvent(BlockStateProvider provider) {
        this(provider, DEFAULT_TOP, DEFAULT_BOTTOM);
    }

    public PillarLuckyEvent(BlockState state) {
        this(BlockStateProvider.of(state));
    }

    public PillarLuckyEvent(Block block) {
        this(BlockStateProvider.of(block));
    }

    public void trigger(ServerWorld world, @Nullable PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        world.setBlockState(pos, stateProvider.get(world.getRandom(), pos), Block.NOTIFY_ALL);
        HeightContext heightContext = new HeightContext(world.getChunkManager().getChunkGenerator(), world);
        var topY = getYPatched(top, pos.getY(), world.getRandom(), heightContext);
        var bottomY = getYPatched(bottom, pos.getY(), world.getRandom(), heightContext);
        for (int y = bottomY; y < topY; y++) {
            world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), stateProvider.get(world.getRandom(), pos), Block.NOTIFY_ALL);
        }
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.WORLD_PILLAR;
    }

    private static int getYPatched(HeightProvider provider, int baseY, Random random, HeightContext heightContext) {
        //IDK how to make it relative to the baseY, so we just add it
        var value = provider.get(random, heightContext);
        if(value == heightContext.getHeight() || value == heightContext.getMinY()) {
            return value;
        }
        return Math.min(heightContext.getHeight(), Math.max(heightContext.getMinY(), value + baseY));
    }
}
