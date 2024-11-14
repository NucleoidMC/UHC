package com.hugman.uhc.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record EntityLootModifier(boolean replace, RegistryEntryList<EntityType<?>> entities,
                                 Optional<RegistryKey<LootTable>> lootTable) implements Modifier {
    public static final MapCodec<EntityLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("replace", true).forGetter(EntityLootModifier::replace),
            RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).optionalFieldOf("entities", RegistryEntryList.empty()).forGetter(EntityLootModifier::entities),
            RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).optionalFieldOf("loot_table").forGetter(EntityLootModifier::lootTable)
    ).apply(instance, EntityLootModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.ENTITY_LOOT;
    }

    public boolean test(LivingEntity livingEntity) {
        return livingEntity.getType().isIn(this.entities);
    }

    public List<ItemStack> getLoots(ServerWorld world, LivingEntity livingEntity) {
        if (this.lootTable.isEmpty()) {
            return Collections.emptyList();
        }

        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(this.lootTable.get());
        LootWorldContext lootContext = new LootWorldContext.Builder(world)
                .add(LootContextParameters.THIS_ENTITY, livingEntity)
                .add(LootContextParameters.ORIGIN, livingEntity.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, livingEntity.getDamageSources().generic())
                .build(LootContextTypes.ENTITY);
        return lootTable.generateLoot(lootContext);
    }

    public boolean shouldReplace() {
        return replace;
    }
}
