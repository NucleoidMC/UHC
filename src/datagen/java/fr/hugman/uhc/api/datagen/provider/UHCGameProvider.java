package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.config.UHCGameConfigs;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
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
        for (UHCGameTeamSize teamSize : UHCGameTeamSize.values()) {
            registerable.register(UHCGameConfigs.of("uhc/" + teamSize.getName()), createUHC(uhc, teamSize));
            registerable.register(UHCGameConfigs.of("uhcrun/" + teamSize.getName()), createUHC(uhcRun, teamSize));
            registerable.register(UHCGameConfigs.of("doublerunner/" + teamSize.getName()), createUHC(doublerunner, teamSize));
        }
    }

    private static GameConfig<?> createUHC(RegistryEntry<UHCConfig> config, UHCGameTeamSize teamSize) {
        return new GameConfig<>(
                UHCGameTypes.STANDARD,
                Text.translatable("game.generic.mode", Text.translatable("game." + config.getKey().get().getValue().getPath()), Text.translatable("mode." + teamSize.getName())),
                null, null, new ItemStack(Items.GRASS_BLOCK), CustomValuesConfig.empty(),
                new UHCGameConfig(
                        new WaitingLobbyConfig(new PlayerLimiterConfig(), teamSize.getMinPlayers(), teamSize.getThresholdPlayers(), WaitingLobbyConfig.Countdown.DEFAULT),
                        teamSize.getTeamsize(),
                        config
                )
        );
    }
}
