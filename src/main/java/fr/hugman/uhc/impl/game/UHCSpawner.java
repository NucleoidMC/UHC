package fr.hugman.uhc.impl.game;

import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.api.util.ColoredBlocks;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class UHCSpawner {
    private final ServerLevel world;
    private final Map<GameTeam, BlockBounds> cages = new HashMap<>();

    public UHCSpawner(ServerLevel world) {
        this.world = world;
    }

    public static Vec3 getSurfaceBlock(ServerLevel world, int x, int z) {
        LevelChunk chunk = world.getChunkAt(new BlockPos(x, 0, z));
        return new Vec3(x, chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 1, z);
    }

    public void spawnPlayerAtCenter(ServerPlayer player) {
        this.spawnPlayerAt(player, this.getSurfaceBlock(0, 0));
    }

    public void spawnPlayerAt(ServerPlayer player, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        this.world.getChunkSource().addTicket(new Ticket(TicketType.PLAYER_SIMULATION, 1), chunkPos);
        player.snapTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
    }

    public void putParticipantInCage(GameTeam team, ServerPlayer participant) {
        BlockBounds bounds = this.cages.get(team);
        if (bounds != null) {
            this.spawnPlayerAt(participant, BlockPos.containing(bounds.centerBottom()).above());
        }
    }

    public void summonCage(GameTeam team, int x, int z) {
        BlockPos pos = new BlockPos(x, 200, z);
        if (this.world.canSeeSky(pos)) {
            pos = new BlockPos(x, 200, z);
        }
        this.addCageAt(team, pos, Blocks.BARRIER.defaultBlockState(), 3, 4);
    }

    public void addCageAt(GameTeam team, BlockPos origin, BlockState sides, int width, int height) {
        ServerLevel world = this.world;
        BlockState floor = ColoredBlocks.glass(team.config().blockDyeColor()).defaultBlockState();

        BlockBounds fullCage = BlockBounds.of(origin.below().north(width).east(width), origin.above(height).south(width).west(width));
        BlockBounds cageFloor = BlockBounds.of(origin.below().north(width - 1).east(width - 1), origin.below().south(width - 1).west(width - 1));
        BlockBounds cageAir = BlockBounds.of(origin.north(width - 1).east(width - 1), origin.above(height - 1).south(width - 1).west(width - 1));

        fullCage.forEach(pos -> world.setBlockAndUpdate(pos, sides));
        cageFloor.forEach(pos -> world.setBlockAndUpdate(pos, floor));
        cageAir.forEach(pos -> world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()));

        this.cages.put(team, fullCage);
    }

    public void clearCages() {
        this.cages.values().forEach(bounds -> bounds.forEach(pos -> this.world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())));
    }

    public BlockPos getSurfaceBlock(int x, int z) {
        return BlockPos.containing(getSurfaceBlock(world, x, z));
    }
}
