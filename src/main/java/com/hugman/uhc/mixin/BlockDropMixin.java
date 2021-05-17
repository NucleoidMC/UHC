package com.hugman.uhc.mixin;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.BlockLootModulePiece;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Block.class)
//TODO: Use BlockEvents.DROP_ITEMS from stimuli
public class BlockDropMixin {
	private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, ServerWorld world, BlockPos pos, Entity entity, ItemStack stack) {
		ManagedGameSpace gameSpace = ManagedGameSpace.forWorld(world);

		boolean keepOld = true;
		if(gameSpace != null && gameSpace.getGameConfig().getConfig() instanceof UHCConfig) {
			UHCConfig config = (UHCConfig) gameSpace.getGameConfig().getConfig();
			for(BlockLootModulePiece piece : config.blockLootModulePieces) {
				if(piece.test(state, world.getRandom())) {
					piece.spawnExperience(world, pos);
					List<ItemStack> newStack = piece.getLoots(world, pos, entity, stack);
					newStack.forEach(action);
					if(piece.replace()) keepOld = false;
				}
			}
		}
		if(keepOld) stacks.forEach(action);
	}

	@Redirect(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
	private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack) {
		if(world instanceof ServerWorld) {
			dropStacks(stacks, action, state, (ServerWorld) world, pos, entity, stack);
		}
	}

	@Redirect(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
	private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, WorldAccess world, BlockPos pos) {
		if(world instanceof ServerWorld) {
			dropStacks(stacks, action, state, (ServerWorld) world, pos, null, ItemStack.EMPTY);
		}
	}

	@Redirect(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
	private static void dropStacks(List<ItemStack> stacks, Consumer<ItemStack> action, BlockState state, World world, BlockPos pos) {
		if(world instanceof ServerWorld) {
			dropStacks(stacks, action, state, (ServerWorld) world, pos, null, ItemStack.EMPTY);
		}
	}
}
