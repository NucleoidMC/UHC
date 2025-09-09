package fr.hugman.ultimate_lucky_block.api.datagen.provider;

import fr.hugman.ultimate_lucky_block.api.config.ULBGameConfigs;
import fr.hugman.ultimate_lucky_block.api.config.ULBUHCConfigs;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCGameConfig;
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

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBGameProvider extends FabricDynamicRegistryProvider {
    public ULBGameProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(GameConfigs.REGISTRY_KEY);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(UltimateLuckyBlock.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Game Configurations (Lucky)";
    }


    public static void register(Registerable<GameConfig<?>> registerable) {
        final var configs = registerable.getRegistryLookup(UHCRegistryKeys.UHC_CONFIG);

        var uhc = configs.getOrThrow(ULBUHCConfigs.LUCKY_UHC);
        var uhcRun = configs.getOrThrow(ULBUHCConfigs.LUCKY_UHCRUN);
        var doublerunner = configs.getOrThrow(ULBUHCConfigs.LUCKY_DOUBLERUNNER);
        for (UHCGameTeamSize teamSize : UHCGameTeamSize.values()) {
            registerable.register(ULBGameConfigs.of("lucky_uhc/" + teamSize.getName()), createUHC(uhc, teamSize));
            registerable.register(ULBGameConfigs.of("lucky_uhcrun/" + teamSize.getName()), createUHC(uhcRun, teamSize));
            registerable.register(ULBGameConfigs.of("lucky_doublerunner/" + teamSize.getName()), createUHC(doublerunner, teamSize));
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
