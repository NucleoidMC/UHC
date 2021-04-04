package com.hugman.uhc;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.phase.UHCWaiting;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class UHC implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("uhc");

	@Override
	public void onInitialize() {
		GameType.register(new Identifier("uhc", "uhc"), UHCWaiting::open, UHCConfig.CODEC);
	}
}
