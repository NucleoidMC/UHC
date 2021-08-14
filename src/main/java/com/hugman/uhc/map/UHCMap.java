package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class UHCMap {
	private final UHCConfig config;
	private final ChunkGenerator chunkGenerator;

	public UHCMap(UHCConfig config, MinecraftServer server) {
		this.config = config;
		this.chunkGenerator = new UHCChunkGenerator(server, config);
	}

	public ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}
}
