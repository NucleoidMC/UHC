package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record EntityLootModifier(
        boolean replace,
        HolderSet<EntityType<?>> entities,
        Optional<ResourceKey<LootTable>> lootTable
) implements Modifier {
    public static final MapCodec<EntityLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("replace", true).forGetter(EntityLootModifier::replace),
            RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("entities", HolderSet.empty()).forGetter(EntityLootModifier::entities),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("loot_table").forGetter(EntityLootModifier::lootTable)
    ).apply(instance, EntityLootModifier::new));

    public EntityLootModifier(
            HolderSet<EntityType<?>> entities,
            ResourceKey<LootTable> lootTable
    ) {
        this(true, entities, Optional.of(lootTable));
    }

    @Override
    public ModifierType<?> getType() {
        return ModifierType.ENTITY_LOOT;
    }

    public boolean test(LivingEntity livingEntity) {
        return livingEntity.is(this.entities);
    }

    public List<ItemStack> getLoots(ServerLevel level, LivingEntity livingEntity) {
        if (this.lootTable.isEmpty()) {
            return Collections.emptyList();
        }

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.lootTable.get());
        LootParams lootContext = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, livingEntity)
                .withParameter(LootContextParams.ORIGIN, livingEntity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, livingEntity.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        return lootTable.getRandomItems(lootContext);
    }

    public boolean shouldReplace() {
        return replace;
    }
}
