package fr.hugman.uhc.data.provider;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.config.UHCGameConfigs;
import fr.hugman.uhc.api.game.UHCGameTypes;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

import java.util.concurrent.CompletableFuture;

public class UHCGameProvider extends FabricDynamicRegistryProvider {
    public UHCGameProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(GameConfigs.REGISTRY_KEY));
    }

    @Override
    public String getName() {
        return "Game Configurations";
    }


    public static void register(Registerable<GameConfig<?>> registerable) {
        final var configs = registerable.getRegistryLookup(UHCRegistryKeys.UHC_CONFIG);

        var uhc = configs.getOrThrow(UHCConfigs.STANDARD_UHC);
        var uhcRun = configs.getOrThrow(UHCConfigs.STANDARD_UHCRUN);
        var doublerunner = configs.getOrThrow(UHCConfigs.STANDARD_DOUBLERUNNER);
        for (TeamSize teamSize : TeamSize.values()) {
            registerable.register(UHCGameConfigs.of("uhc/" + teamSize.name), createUHC(uhc, teamSize));
            registerable.register(UHCGameConfigs.of("uhcrun/" + teamSize.name), createUHC(uhcRun, teamSize));
            registerable.register(UHCGameConfigs.of("doublerunner/" + teamSize.name), createUHC(doublerunner, teamSize));
        }
    }

    private static GameConfig<?> createUHC(RegistryEntry<UHCConfig> config, TeamSize teamSize) {
        return new GameConfig<>(
                UHCGameTypes.STANDARD,
                Text.translatable("game.generic.mode", Text.translatable("game." + config.getKey().get().getValue().getPath()), Text.translatable("mode." + teamSize.name)),
                null, null, new ItemStack(Items.GRASS_BLOCK), CustomValuesConfig.empty(),
                new UHCGameConfig(
                        new WaitingLobbyConfig(new PlayerLimiterConfig(), teamSize.minPlayers, teamSize.thresholdPlayers, WaitingLobbyConfig.Countdown.DEFAULT),
                        teamSize.teamsize,
                        config
                )
        );
    }

    private enum TeamSize {
        SOLO("solo", 1, 2, 8),
        DUOS("duos", 2, 4, 16),
        TRIOS("trios", 3, 6, 24),
        SQUADS("squads", 4, 8, 32);

        private final String name;
        private final int teamsize;
        private final int minPlayers;
        private final int thresholdPlayers;

        TeamSize(String name, int teamsize, int minPlayers, int thresholdPlayers) {
            this.name = name;
            this.teamsize = teamsize;
            this.minPlayers = minPlayers;
            this.thresholdPlayers = thresholdPlayers;
        }
    }
}
