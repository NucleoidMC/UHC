package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class UHCMap {
	private final UHCConfig config;
	private final long seed;
	private final ChunkGenerator chunkGenerator;

	public UHCMap(UHCConfig config, MinecraftServer server) {
		this.config = config;
		this.seed = server.getOverworld().getRandom().nextLong();
		this.chunkGenerator = new UHCChunkGenerator(server, config, seed);
	}

	public long getSeed() {
		return seed;
	}

	public ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}
}
