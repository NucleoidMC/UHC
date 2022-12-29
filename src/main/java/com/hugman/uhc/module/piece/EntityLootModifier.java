package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityLootModifier implements Modifier {
	public static final Codec<EntityLootModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", true).forGetter(module -> module.replace),
			TagKey.codec(RegistryKeys.ENTITY_TYPE).optionalFieldOf("tag").forGetter(module -> module.tag),
			Registries.ENTITY_TYPE.getCodec().optionalFieldOf("entity").forGetter(module -> module.entity),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable)
	).apply(instance, EntityLootModifier::new));

	private final boolean replace;
	private final Optional<TagKey<EntityType<?>>> tag;
	private final Optional<EntityType<?>> entity;
	private final Identifier lootTable;

	private EntityLootModifier(boolean replace, Optional<TagKey<EntityType<?>>> tag, Optional<EntityType<?>> entity, Identifier lootTable) {
		this.replace = replace;
		this.tag = tag;
		this.entity = entity;
		this.lootTable = lootTable;
	}

	@Override
	public ModifierType<?> getType() {
		return ModifierType.ENTITY_LOOT;
	}

	public boolean test(LivingEntity livingEntity) {
		boolean valid = false;
		if (this.tag.isPresent()) {
			if (livingEntity.getType().isIn(this.tag.get())) {
				valid = true;
			}
		} else if (this.entity.isPresent()) {
			EntityType<?> entityType = this.entity.get();
			if (entityType.equals(livingEntity.getType())) {
				valid = true;
			}
		}
		return valid;
	}

	public List<ItemStack> getLoots(ServerWorld world, LivingEntity livingEntity) {
		LootContext.Builder builder = new LootContext.Builder(world).random(livingEntity.getRandom()).parameter(LootContextParameters.THIS_ENTITY, livingEntity).parameter(LootContextParameters.ORIGIN, livingEntity.getPos()).parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.GENERIC);
		if (this.lootTable == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.build(LootContextTypes.ENTITY);
			LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(this.lootTable);
			return lootTable.generateLoot(lootContext);
		}
	}

	public boolean shouldReplace() {
		return replace;
	}
}
