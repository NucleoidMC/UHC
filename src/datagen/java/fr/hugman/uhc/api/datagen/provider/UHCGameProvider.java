package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCGameConfigs;
import fr.hugman.uhc.api.datagen.compat.ULBUHCCompat;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.registry.PlasmidRegistryKeys;

import java.util.concurrent.CompletableFuture;

public class UHCGameProvider extends FabricDynamicRegistryProvider {
    public UHCGameProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        ULBUHCCompat.addAll(entries, registries.lookupOrThrow(PlasmidRegistryKeys.GAME_CONFIG));
    }

    @Override
    public String getName() {
        return "Game Configurations";
    }

    public static void register(BootstrapContext<GameConfig<?>> registerable) {
        final var configs = registerable.lookup(UHCRegistryKeys.UHC_CONFIG);

        var uhc = configs.getOrThrow(UHCConfigs.STANDARD_UHC);
        var uhcRun = configs.getOrThrow(UHCConfigs.STANDARD_UHCRUN);
        var doublerunner = configs.getOrThrow(UHCConfigs.STANDARD_DOUBLERUNNER);

        var luckyUhc = configs.getOrThrow(UHCConfigs.LUCKY_UHC);
        var luckyUhcRun = configs.getOrThrow(UHCConfigs.LUCKY_UHCRUN);
        var luckyDoublerunner = configs.getOrThrow(UHCConfigs.LUCKY_DOUBLERUNNER);

        for (UHCGameTeamSize teamSize : UHCGameTeamSize.values()) {
            registerable.register(UHCGameConfigs.of("uhc/" + teamSize.getName()), UHCGameConfigs.create(uhc, teamSize));
            registerable.register(UHCGameConfigs.of("uhcrun/" + teamSize.getName()), UHCGameConfigs.create(uhcRun, teamSize));
            registerable.register(UHCGameConfigs.of("doublerunner/" + teamSize.getName()), UHCGameConfigs.create(doublerunner, teamSize));

            registerable.register(UHCGameConfigs.of("lucky_uhc/" + teamSize.getName()), UHCGameConfigs.create(luckyUhc, teamSize));
            registerable.register(UHCGameConfigs.of("lucky_uhcrun/" + teamSize.getName()), UHCGameConfigs.create(luckyUhcRun, teamSize));
            registerable.register(UHCGameConfigs.of("lucky_doublerunner/" + teamSize.getName()), UHCGameConfigs.create(luckyDoublerunner, teamSize));
        }
    }
}
