package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Lucky event that summons an entity.
 *
 * @author Hugman
 * @since 1.0.0
 */
public record SummonEntityLuckyEvent(
        NbtComponent data,
        boolean shouldTarget,
        boolean tamed
) implements LuckyEvent {
    public static final NbtComponent DEFAULT_DATA = NbtComponent.DEFAULT;
    public static final boolean DEFAULT_SHOUlD_TARGET = false;
    public static final boolean DEFAULT_TAMED = false;

    public static final MapCodec<SummonEntityLuckyEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NbtComponent.CODEC_WITH_ID.optionalFieldOf("data", DEFAULT_DATA).forGetter(SummonEntityLuckyEvent::data),
            Codec.BOOL.optionalFieldOf("should_target", false).forGetter(SummonEntityLuckyEvent::shouldTarget),
            Codec.BOOL.optionalFieldOf("tamed", false).forGetter(SummonEntityLuckyEvent::tamed)
    ).apply(instance, SummonEntityLuckyEvent::new));

    public SummonEntityLuckyEvent(EntityType<?> entityType) {
        this(withType(new NbtCompound(), entityType), DEFAULT_SHOUlD_TARGET, DEFAULT_TAMED);
    }

    public void trigger(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        var entity = EntityType.loadEntityWithPassengers(this.data.copyNbt(), world, SpawnReason.MOB_SUMMONED, e -> {
            e.refreshPositionAndAngles(pos, e.getRandom().nextFloat() * 360.0F, 0.0F);
            return e;
        });
        if (entity == null) {
            LuckyBlockMod.LOGGER.error("Failed to summon entity from NBT: {}", this.data);
            return;
        }

        if (this.tamed && entity instanceof TameableEntity tameable) {
            tameable.setTamedBy(player);
        }
        if (entity instanceof MobEntity mob) {
            mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.MOB_SUMMONED, null);
            if (this.shouldTarget) {
                mob.setTarget(player);
            }
        }
        if (entity instanceof Angerable angerable) {
            if (this.shouldTarget) {
                angerable.setTarget(player);
            }
        }
        if (!world.spawnNewEntityAndPassengers(entity)) {
            LuckyBlockMod.LOGGER.error("Failed to spawn entity: {}", entity);
        }
    }

    @Override
    public LuckyEventType<?> getType() {
        return LuckyEventTypes.SUMMON_ENTITY;
    }

    public static Builder builder(EntityType<?> type) {
        return new Builder(type);
    }

    public static class Builder {
        private final EntityType<?> type;
        private NbtCompound data = DEFAULT_DATA.copyNbt();
        private boolean target = DEFAULT_SHOUlD_TARGET;
        private boolean tamed = DEFAULT_TAMED;

        public Builder(EntityType<?> type) {
            this.type = type;
        }

        public Builder data(NbtCompound data) {
            this.data = data;
            return this;
        }

        public Builder name(String name) {
            this.data.putString("CustomName", name);
            return this;
        }

        public Builder shouldTarget() {
            this.target = true;
            return this;
        }

        public Builder tamed() {
            this.tamed = true;
            return this;
        }

        public SummonEntityLuckyEvent build() {
            return new SummonEntityLuckyEvent(withType(data, type), target, tamed);
        }
    }

    private static NbtComponent withType(NbtCompound compound, EntityType<?> entityType) {
        compound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        return NbtComponent.of(compound);
    }
}
