package com.hugman.uhc;

import com.google.common.reflect.Reflection;
import com.hugman.uhc.command.ModulesCommand;
import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.phase.UHCWaiting;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import com.hugman.uhc.registry.UHCRegistryKeys;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
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
		Reflection.initialize(Module.class);
		Reflection.initialize(ModifierType.class);

		UHCRegistryKeys.registerDynamics();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> ModulesCommand.register(dispatcher));
		GameType.register(UHC.id("standard"), UHCConfig.CODEC, UHCWaiting::open);
	}

	public static void debug(String s) {
		if(debug()) LOGGER.info(s);
	}

	public static boolean debug() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
