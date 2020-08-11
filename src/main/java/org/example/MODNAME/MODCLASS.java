package org.example.MODNAME;

import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.MODNAME.game.MODCLASSConfig;
import org.example.MODNAME.game.MODCLASSWaiting;

public class MODCLASS implements ModInitializer {

    public static final String ID = "MODNAME";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<MODCLASSConfig> TYPE = GameType.register(
            new Identifier(ID, "MODNAME"),
            MODCLASSWaiting::open,
            MODCLASSConfig.CODEC
    );

    @Override
    public void onInitialize() {}
}
