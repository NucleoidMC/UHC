package com.hugman.uhc;

import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.hugman.uhc.game.UHCConfig;
import com.hugman.uhc.game.UHCWaiting;

public class UHC implements ModInitializer {

    public static final String ID = "uhc";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<UHCConfig> TYPE = GameType.register(
            new Identifier(ID, "uhc"),
            UHCWaiting::open,
            UHCConfig.CODEC
    );

    @Override
    public void onInitialize() {}
}
