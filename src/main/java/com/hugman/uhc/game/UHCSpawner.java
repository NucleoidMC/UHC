package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
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
	private final UHCConfig config;
	private final List<BlockBounds> blockBounds = new ArrayList<>();

	public UHCSpawner(GameSpace gameSpace, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.config = config;
	}

	public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
		player.inventory.clear();
		player.getEnderChestInventory().clear();
		player.clearStatusEffects();
		player.setHealth(20.0F);
		player.getHungerManager().setFoodLevel(20);
		player.fallDistance = 0.0F;
		player.setGameMode(gameMode);
		player.setExperienceLevel(0);
		player.setExperiencePoints(0);
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
		BlockPos pos = getSurfaceBlock(x, z);
		if(this.gameSpace.getWorld().isSkyVisible(pos)) {
			pos = new BlockPos(x, 200, z);
		}
		this.addCageAt(pos, ColoredBlocks.glass(DyeColor.byId(random.nextInt(15))).getDefaultState(), Blocks.BARRIER.getDefaultState(), 2, 4);
		this.spawnPlayerAt(player, pos);
	}

	public void addCageAt(BlockPos origin, BlockState floor, BlockState sides, int width, int height) {
		BlockBounds cage = new BlockBounds(origin.down().north(width).east(width), origin.up(height).south(width).west(width));
		cage.forEach(pos -> this.gameSpace.getWorld().setBlockState(pos, sides));
		BlockBounds cageFloor = new BlockBounds(origin.down().north(width).east(width), origin.down().south(width).west(width));
		cageFloor.forEach(pos -> this.gameSpace.getWorld().setBlockState(pos, floor));
		BlockBounds cageAir = new BlockBounds(origin.north(width - 1).east(width - 1), origin.up(height - 1).south(width - 1).west(width - 1));
		cageAir.forEach(pos -> this.gameSpace.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState()));
		this.blockBounds.add(cage);
	}

	public void clearCages() {
		this.blockBounds.forEach(bounds -> bounds.forEach(pos -> this.gameSpace.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState())));
	}

	public BlockPos getSurfaceBlock(int x, int z) {
		WorldChunk chunk = this.gameSpace.getWorld().getWorldChunk(new BlockPos(x, 0, z));

		return new BlockPos(x, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 1, z);
	}
}
