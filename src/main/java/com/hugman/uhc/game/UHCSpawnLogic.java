package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
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

	public void putParticipantInCageAt(ServerPlayerEntity player, int x, int z) {
		BlockPos pos = new BlockPos(x, 200, z);
		ServerWorld world = this.gameSpace.getWorld();
		BlockBounds cage = new BlockBounds(pos.down().north(3).east(3), pos.up(4).south(3).west(3));
		cage.forEach(pos1 -> world.setBlockState(pos1, Blocks.GLASS.getDefaultState()));
		BlockBounds cageAir = new BlockBounds(pos.north(2).east(2), pos.up(3).south(2).west(2));
		cageAir.forEach(pos1 -> world.setBlockState(pos1, Blocks.AIR.getDefaultState()));
		blockBounds.add(cage);
		this.spawnPlayerAt(player, pos);
	}

	public void clearCages() {
		ServerWorld world = this.gameSpace.getWorld();
		this.blockBounds.forEach(bounds -> bounds.forEach(pos -> world.setBlockState(pos, Blocks.AIR.getDefaultState())));
	}

	public void spawnPlayerAt(ServerPlayerEntity player, BlockPos pos) {
		ServerWorld world = this.gameSpace.getWorld();
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getEntityId());
		player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
	}

	public BlockPos getSurfaceBlock(int x, int z) {
		ServerWorld world = this.gameSpace.getWorld();

		ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
		WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

		return new BlockPos(x, chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 1, z);
	}
}
