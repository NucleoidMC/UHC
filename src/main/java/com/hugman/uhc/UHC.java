package com.hugman.uhc;

import com.hugman.uhc.command.ModulesCommand;
import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.phase.UHCWaiting;
import com.hugman.uhc.module.UHCModule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class UHC implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();

	public static Identifier id(String s) {
		return new Identifier("uhc", s);
	}

	@Override
	public void onInitialize() {
		UHCModule.register();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> ModulesCommand.register(dispatcher));
		GameType.register(UHC.id("uhc"), UHCConfig.CODEC, UHCWaiting::open);
	}
}
