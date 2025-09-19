package fr.hugman.uhc.api.config;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.registry.RegistryKey;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

public class UHCGameConfigs {
    public static RegistryKey<GameConfig<?>> of(String path) {
        return RegistryKey.of(GameConfigs.REGISTRY_KEY, UHC.id(path));
    }
}
