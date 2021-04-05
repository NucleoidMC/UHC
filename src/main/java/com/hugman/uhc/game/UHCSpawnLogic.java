package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.ArrayList;
import java.util.List;

public final class UHCSpawnLogic {
	private final GameSpace gameSpace;
	private final UHCConfig config;
	private final List<BlockBounds> blockBounds = new ArrayList<>();

	public UHCSpawnLogic(GameSpace gameSpace, UHCConfig config) {
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
		BlockPos pos = getSurfaceBlock(x, z);
		if(this.gameSpace.getWorld().isSkyVisible(pos)) {
			pos = new BlockPos(x, 200, z);
		}
		this.addCageAt(pos, Blocks.BARRIER.getDefaultState());
		this.spawnPlayerAt(player, pos);
	}

	public void addCageAt(BlockPos pos, BlockState state) {
		BlockBounds cage = new BlockBounds(pos.down().north(3).east(3), pos.up(4).south(3).west(3));
		cage.forEach(pos1 -> this.gameSpace.getWorld().setBlockState(pos1, state));
		BlockBounds cageAir = new BlockBounds(pos.north(2).east(2), pos.up(3).south(2).west(2));
		cageAir.forEach(pos1 -> this.gameSpace.getWorld().setBlockState(pos1, Blocks.AIR.getDefaultState()));
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
