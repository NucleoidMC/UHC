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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record EntityLootModulePiece(boolean replace, Optional<Tag<EntityType<?>>> tag, Optional<EntityType<?>> entity, Identifier lootTable) implements ModulePiece {
	public static final Codec<EntityLootModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", true).forGetter(module -> module.replace),
			Tag.codec(() -> ServerTagManagerHolder.getTagManager().getOrCreateTagGroup(Registry.ENTITY_TYPE_KEY)).optionalFieldOf("tag").forGetter(module -> module.tag),
			Registry.ENTITY_TYPE.getCodec().optionalFieldOf("entity").forGetter(module -> module.entity),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable)
	).apply(instance, EntityLootModulePiece::new));

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public boolean test(LivingEntity livingEntity) {
		boolean valid = false;
		if(tag.isPresent()) {
			Tag<EntityType<?>> entityTypeTag = tag.get();
			if(entityTypeTag.contains(livingEntity.getType())) {
				valid = true;
			}
		}
		else if(entity.isPresent()) {
			EntityType<?> entityType = entity.get();
			if(entityType.equals(livingEntity.getType())) {
				valid = true;
			}
		}
		return valid;
	}

	public List<ItemStack> getLoots(ServerWorld world, LivingEntity livingEntity) {
		LootContext.Builder builder = new LootContext.Builder(world).random(livingEntity.getRandom()).parameter(LootContextParameters.THIS_ENTITY, livingEntity).parameter(LootContextParameters.ORIGIN, livingEntity.getPos()).parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.GENERIC);
		if(this.lootTable == LootTables.EMPTY) {
			return Collections.emptyList();
		}
		else {
			LootContext lootContext = builder.build(LootContextTypes.ENTITY);
			LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(this.lootTable);
			return lootTable.generateLoot(lootContext);
		}
	}

}
