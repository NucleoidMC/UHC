package com.hugman.lucky_block.lucky_event;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Lucky event that drops the content of a loot table.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record LootLuckyEvent(RegistryKey<LootTable> lootTable) implements LuckyEvent {
    public static final MapCodec<LootLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(LootLuckyEvent::lootTable)
    ).apply(instance, LootLuckyEvent::new));

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        getLoots(world, pos, player, player.getMainHandStack()).forEach((stack) -> Block.dropStack(world, pos, stack));
    }

    public List<ItemStack> getLoots(ServerWorld world, BlockPos pos, @Nullable Entity entity, ItemStack stack) {
        return world.getServer().getReloadableRegistries().getLootTable(this.lootTable)
                .generateLoot(new LootWorldContext.Builder(world)
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .add(LootContextParameters.TOOL, stack)
                        .add(LootContextParameters.BLOCK_STATE, world.getBlockState(pos))
                        .addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos))
                        .addOptional(LootContextParameters.THIS_ENTITY, entity)
                        .build(LootContextTypes.BLOCK));
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.LOOT;
    }
}
