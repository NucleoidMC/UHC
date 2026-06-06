package fr.hugman.uhc.api.config;

import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.game.UHCGameTypes;
import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.registry.PlasmidRegistryKeys;

public class UHCGameConfigs {
    public static ResourceKey<GameConfig<?>> of(String path) {
        return ResourceKey.create(PlasmidRegistryKeys.GAME_CONFIG, UHC.id(path));
    }

    public static GameConfig<?> create(Holder<UHCConfig> config, UHCGameTeamSize teamSize) {
        return new GameConfig<>(
                UHCGameTypes.STANDARD,
                Component.translatable("game.generic.mode", Component.translatable("game." + config.unwrapKey().get().identifier().getPath()), Component.translatable("mode." + teamSize.getName())),
                null,
                null,
                new ItemStackTemplate(Items.GRASS_BLOCK),
                CustomValuesConfig.empty(),
                new UHCGameConfig(
                        new WaitingLobbyConfig(new PlayerLimiterConfig(), teamSize.getMinPlayers(), teamSize.getThresholdPlayers(), WaitingLobbyConfig.Countdown.DEFAULT),
                        teamSize.getTeamsize(),
                        config
                )
        );
    }
}
