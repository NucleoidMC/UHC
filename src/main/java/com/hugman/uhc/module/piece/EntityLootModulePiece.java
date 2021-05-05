package com.hugman.uhc.module.piece;

import com.hugman.uhc.game.phase.UHCActive;
import com.mojang.datafixers.util.Either;
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

public class EntityLootModulePiece implements ModulePiece {
	public static final Codec<EntityLootModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(Identifier.CODEC, Tag.codec(() -> ServerTagManagerHolder.getTagManager().getEntityTypes())).fieldOf("target").forGetter(module -> module.target),
			Identifier.CODEC.optionalFieldOf("loot_table", LootTables.EMPTY).forGetter(module -> module.lootTable)
	).apply(instance, EntityLootModulePiece::new));

	private final Either<Identifier, Tag<EntityType<?>>> target;
	private final Identifier lootTable;

	public EntityLootModulePiece(Either<Identifier, Tag<EntityType<?>>> target, Identifier lootTable) {
		this.target = target;
		this.lootTable = lootTable;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public List<ItemStack> modifyLoots(UHCActive active, LivingEntity livingEntity, List<ItemStack> itemStacks) {
		ServerWorld world = active.gameSpace.getWorld();

		boolean valid = false;
		if(target.right().isPresent()) {
			Tag<EntityType<?>> entityTypeTag = target.right().get();
			if(entityTypeTag.contains(livingEntity.getType())) {
				valid = true;
			}
		}
		if(target.left().isPresent()) {
			Identifier typeName = target.left().get();
			if(typeName.equals(Registry.ENTITY_TYPE.getId(livingEntity.getType()))) {
				valid = true;
			}
		}
		if(valid) {
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
		return itemStacks;
	}

}
