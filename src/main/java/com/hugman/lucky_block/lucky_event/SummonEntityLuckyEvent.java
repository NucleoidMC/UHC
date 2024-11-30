package com.hugman.lucky_block.lucky_event;

import com.hugman.lucky_block.LuckyBlockMod;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event that summons an entity.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record SummonEntityLuckyEvent(NbtComponent data) implements LuckyEvent {
    public static final MapCodec<SummonEntityLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NbtComponent.CODEC.optionalFieldOf("data", NbtComponent.DEFAULT).forGetter(SummonEntityLuckyEvent::data)
    ).apply(instance, SummonEntityLuckyEvent::new));

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        var entity = EntityType.loadEntityWithPassengers(this.data.copyNbt(), world, SpawnReason.MOB_SUMMONED, entityx -> {
            entityx.refreshPositionAndAngles(pos, entityx.getRandom().nextFloat() * 360.0F, 0.0F);
            return entityx;
        });
        if (entity == null) {
            LuckyBlockMod.LOGGER.error("Failed to summon entity from NBT: {}", this.data);
            return;
        }

        if (entity instanceof MobEntity mob) {
            mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.MOB_SUMMONED, null);
            mob.setTarget(player);
        }
        if (entity instanceof Angerable angerable) {
            angerable.setTarget(player);
        }
        if (!world.spawnNewEntityAndPassengers(entity)) {
            LuckyBlockMod.LOGGER.error("Failed to spawn entity: {}", entity);
        }
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.SUMMON_ENTITY;
    }
}
