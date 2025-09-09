package fr.hugman.ultimate_lucky_block.api.config;

import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.RegistryKey;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBGameConfigs {
    public static RegistryKey<GameConfig<?>> of(String path) {
        return RegistryKey.of(GameConfigs.REGISTRY_KEY, UltimateLuckyBlock.id(path));
    }
}
