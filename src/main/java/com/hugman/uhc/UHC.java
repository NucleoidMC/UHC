package com.hugman.uhc;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.module.Modules;
import com.hugman.uhc.game.phase.UHCWaiting;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class UHC implements ModInitializer {
	private static final String MOD_ID = "uhc";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static Identifier id(String s) {
		return new Identifier(MOD_ID, s);
	}

	@Override
	public void onInitialize() {
		Modules.register();
		GameType.register(UHC.id("standard"), UHCWaiting::open, UHCConfig.CODEC);
	}
}
