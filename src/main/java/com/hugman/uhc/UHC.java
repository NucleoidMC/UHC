package com.hugman.uhc;

import com.google.common.reflect.Reflection;
import com.hugman.uhc.command.UHCCommand;
import com.hugman.uhc.config.UHCGameConfig;
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
import xyz.nucleoid.plasmid.api.game.GameType;

public class UHC implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        Reflection.initialize(Module.class);
        Reflection.initialize(ModifierType.class);

        UHCRegistryKeys.registerDynamics();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UHCCommand.register(dispatcher, registryAccess));
        GameType.register(UHC.id("standard"), UHCGameConfig.CODEC, UHCWaiting::open);
    }

    public static Identifier id(String path) {
        return Identifier.of("uhc", path);
    }

    public static void debug(String msg) {
        if (debug()) LOGGER.info(msg);
    }

    public static boolean debug() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
