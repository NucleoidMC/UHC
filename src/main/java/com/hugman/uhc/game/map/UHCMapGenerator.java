package com.hugman.uhc.game.map;

import com.hugman.uhc.config.UHCMapConfig;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

public class UHCMapGenerator {

	private final UHCMapConfig config;

	public UHCMapGenerator(UHCMapConfig config) {
		this.config = config;
	}

	public UHCMap build() {
		MapTemplate template = MapTemplate.createEmpty();
		UHCMap map = new UHCMap(this.config);

		this.buildSpawn(template);

		return map;
	}
}
