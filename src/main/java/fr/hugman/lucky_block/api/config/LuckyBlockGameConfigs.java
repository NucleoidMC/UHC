package fr.hugman.lucky_block.api.config;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import net.minecraft.registry.RegistryKey;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

public class LuckyBlockGameConfigs {
    public static RegistryKey<GameConfig<?>> of(String path) {
        return RegistryKey.of(GameConfigs.REGISTRY_KEY, LuckyBlockMod.id(path));
    }
}
