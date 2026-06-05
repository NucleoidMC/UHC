package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record BlockLootModifier(
        boolean replace,
        RuleTest predicate,
        Optional<ResourceKey<LootTable>> lootTable,
        int experience
) implements Modifier {
    public static final MapCodec<BlockLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("replace", true).forGetter(BlockLootModifier::replace),
            RuleTest.CODEC.fieldOf("target").forGetter(BlockLootModifier::predicate),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("loot_table").forGetter(BlockLootModifier::lootTable),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("experience", 0).forGetter(BlockLootModifier::experience)
    ).apply(instance, BlockLootModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.BLOCK_LOOT;
    }

    public boolean test(BlockState state, RandomSource random) {
        return this.predicate.test(state, random);
    }

    public void spawnExperience(ServerLevel world, BlockPos pos) {
        int xp = this.experience;
        while (xp > 0) {
            int i = ExperienceOrb.getExperienceValue(xp);
            xp -= i;
            world.addFreshEntity(new ExperienceOrb(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, i));
        }
    }

    public List<ItemStack> getLoots(ServerLevel world, BlockPos pos, @Nullable Entity entity, ItemStack stack) {
        if (this.lootTable.isEmpty()) {
            return Collections.emptyList();
        }

        LootTable lootTable = world.getServer().reloadableRegistries().getLootTable(this.lootTable.get());
        LootParams lootContext = new LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, stack)
                .withParameter(LootContextParams.BLOCK_STATE, world.getBlockState(pos))
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                .create(LootContextParamSets.BLOCK);
        return lootTable.getRandomItems(lootContext);
    }

    public boolean shouldReplace() {
        return replace;
    }
}
