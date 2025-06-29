package fr.hugman.uhc;

import com.google.common.reflect.Reflection;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.api.module.Module;
import fr.hugman.uhc.api.registry.UHCRegistries;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.impl.command.ModulesCommand;
import fr.hugman.uhc.impl.game.phase.UHCWaiting;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.api.game.GameType;

public class UHC implements ModInitializer {
    public static final String MOD_ID = "uhc";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        Reflection.initialize(Module.class);
        Reflection.initialize(ModifierType.class);

        UHCRegistries.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ModulesCommand.register(dispatcher));
        GameType.register(UHC.id("standard"), UHCGameConfig.CODEC, UHCWaiting::open);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void debug(String msg) {
        if (debug()) LOGGER.info(msg);
    }

    public static boolean debug() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
