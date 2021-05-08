package com.hugman.uhc.game;

import com.hugman.uhc.module.piece.ModulePieceManager;
import com.hugman.uhc.module.piece.PermanentEffectModulePiece;
import com.hugman.uhc.module.piece.PlayerAttributeModulePiece;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.ColoredBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class UHCSpawner {
	private final GameSpace gameSpace;
	private final ModulePieceManager modulePieceManager;
	private final List<BlockBounds> blockBounds = new ArrayList<>();

	public UHCSpawner(GameSpace gameSpace, ModulePieceManager modulePieceManager) {
		this.gameSpace = gameSpace;
		this.modulePieceManager = modulePieceManager;
	}

	public void resetPlayer(ServerPlayerEntity player, GameMode gameMode, boolean clear) {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			for(PlayerAttributeModulePiece piece : this.modulePieceManager.playerAttributeModulePieces) {
				piece.setAttribute(player);
			}
		}
		if(clear) {
			player.inventory.clear();
			player.getEnderChestInventory().clear();
			player.clearStatusEffects();
			player.getHungerManager().setFoodLevel(20);
			player.setExperienceLevel(0);
			player.setExperiencePoints(0);
			player.setHealth(player.getMaxHealth());
		}
		player.extinguish();
		player.fallDistance = 0.0F;
		player.setGameMode(gameMode);
	}

	public void applyEffects(ServerPlayerEntity player, int effectDuration) {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			for(PermanentEffectModulePiece piece : this.modulePieceManager.permanentEffectModulePieces) {
				piece.setEffect(player, effectDuration);
			}
		}
	}

	public void spawnPlayerAtCenter(ServerPlayerEntity player) {
		this.spawnPlayerAt(player, this.getSurfaceBlock(0, 0));
	}

	public void spawnPlayerAt(ServerPlayerEntity player, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		this.gameSpace.getWorld().getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getEntityId());
		player.teleport(this.gameSpace.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
	}

	public void summonPlayerInCageAt(ServerPlayerEntity player, int x, int z) {
		Random random = player.getRandom();
		BlockPos pos = new BlockPos(x, 200, z);
		if(this.gameSpace.getWorld().isSkyVisible(pos)) {
			pos = new BlockPos(x, 200, z);
		}
		this.addCageAt(pos, ColoredBlocks.glass(DyeColor.byId(random.nextInt(15))).getDefaultState(), Blocks.BARRIER.getDefaultState(), 3, 4);
		this.spawnPlayerAt(player, pos);
	}

	public void addCageAt(BlockPos origin, BlockState floor, BlockState sides, int width, int height) {
		ServerWorld world = this.gameSpace.getWorld();

		BlockBounds fullCage = new BlockBounds(origin.down().north(width).east(width), origin.up(height).south(width).west(width));
		BlockBounds cageFloor = new BlockBounds(origin.down().north(width - 1).east(width - 1), origin.down().south(width - 1).west(width - 1));
		BlockBounds cageAir = new BlockBounds(origin.north(width - 1).east(width - 1), origin.up(height - 1).south(width - 1).west(width - 1));

		fullCage.forEach(pos -> world.setBlockState(pos, sides));
		cageFloor.forEach(pos -> world.setBlockState(pos, floor));
		cageAir.forEach(pos -> world.setBlockState(pos, Blocks.AIR.getDefaultState()));

		this.blockBounds.add(fullCage);
	}

	public void clearCages() {
		this.blockBounds.forEach(bounds -> bounds.forEach(pos -> this.gameSpace.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState())));
	}

	public BlockPos getSurfaceBlock(int x, int z) {
		WorldChunk chunk = this.gameSpace.getWorld().getWorldChunk(new BlockPos(x, 0, z));

		return new BlockPos(x, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 1, z);
	}
}
