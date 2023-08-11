package com.hugman.uhc.map;

import com.hugman.uhc.config.UHCConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

public class UHCMap {
	private final UHCConfig config;
	private final ChunkGenerator chunkGenerator;

	public UHCMap(UHCConfig config, MinecraftServer server) {
		this.config = config;
		//this.chunkGenerator = config.mapConfig().dimension().chunkGenerator(); // temporary fix
		this.chunkGenerator = new ModuledChunkGenerator(server, config);
	}

	public RuntimeWorldConfig createRuntimeWorldConfig() {
		return new RuntimeWorldConfig()
				.setGenerator(this.chunkGenerator)
				.setGameRule(GameRules.NATURAL_REGENERATION, false)
				.setGameRule(GameRules.DO_MOB_SPAWNING, true)
				.setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
				.setDimensionType(this.config.mapConfig().dimension().dimensionTypeEntry());
	}
}
