package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.ColoredBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UHCSpawner {
	private final GameSpace gameSpace;
	private final UHCConfig config;
	private final List<BlockBounds> blockBounds = new ArrayList<>();

	public UHCSpawner(GameSpace gameSpace, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.config = config;
	}

	public void spawnPlayerAtCenter(ServerPlayerEntity player) {
		this.spawnPlayerAt(player, this.getSurfaceBlock(0, 0));
	}

	public void spawnPlayerAt(ServerPlayerEntity player, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		this.gameSpace.getWorld().getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getEntityId());
		player.teleport(this.gameSpace.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
	}

	public void summonPlayerInCageAt(UHCParticipant participant, int x, int z) {
		Random random = participant.getPlayer().getRandom();
		BlockPos pos = new BlockPos(x, 200, z);
		if(this.gameSpace.getWorld().isSkyVisible(pos)) {
			pos = new BlockPos(x, 200, z);
		}
		this.addCageAt(pos, ColoredBlocks.glass(DyeColor.byId(random.nextInt(15))).getDefaultState(), Blocks.BARRIER.getDefaultState(), 3, 4);
		this.spawnPlayerAt(participant.getPlayer(), pos);
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
