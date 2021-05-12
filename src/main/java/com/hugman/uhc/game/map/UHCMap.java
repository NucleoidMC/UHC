package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class UHCMap {
	private final UHCMapConfig config;
	private final ChunkGenerator chunkGenerator;

	public UHCMap(UHCMapConfig config, MinecraftServer server) {
		this.config = config;
		this.chunkGenerator = new UHCChunkGenerator(server, config);
	}

	public ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}
}
