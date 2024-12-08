package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record BlockLootModifier(
        boolean replace,
        RuleTest predicate,
        Optional<RegistryKey<LootTable>> lootTable,
        int experience
) implements Modifier {
    public static final MapCodec<BlockLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("replace", true).forGetter(BlockLootModifier::replace),
            RuleTest.TYPE_CODEC.fieldOf("target").forGetter(BlockLootModifier::predicate),
            RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).optionalFieldOf("loot_table").forGetter(BlockLootModifier::lootTable),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("experience", 0).forGetter(BlockLootModifier::experience)
    ).apply(instance, BlockLootModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.BLOCK_LOOT;
    }

    public boolean test(BlockState state, Random random) {
        return this.predicate.test(state, random);
    }

    public void spawnExperience(ServerWorld world, BlockPos pos) {
        int xp = this.experience;
        while (xp > 0) {
            int i = ExperienceOrbEntity.roundToOrbSize(xp);
            xp -= i;
            world.spawnEntity(new ExperienceOrbEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i));
        }
    }

    public List<ItemStack> getLoots(ServerWorld world, BlockPos pos, @Nullable Entity entity, ItemStack stack) {
        if (this.lootTable.isEmpty()) {
            return Collections.emptyList();
        }

        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(this.lootTable.get());
        LootWorldContext lootContext = new LootWorldContext.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, stack)
                .add(LootContextParameters.BLOCK_STATE, world.getBlockState(pos))
                .addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos))
                .addOptional(LootContextParameters.THIS_ENTITY, entity)
                .build(LootContextTypes.BLOCK);
        return lootTable.generateLoot(lootContext);
    }

    public boolean shouldReplace() {
        return replace;
    }
}
