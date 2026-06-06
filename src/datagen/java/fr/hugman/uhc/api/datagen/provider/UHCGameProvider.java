package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCGameConfigs;
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
        entries.addAll(registries.lookupOrThrow(PlasmidRegistryKeys.GAME_CONFIG));
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
        for (UHCGameTeamSize teamSize : UHCGameTeamSize.values()) {
            registerable.register(UHCGameConfigs.of("uhc/" + teamSize.getName()), UHCGameConfigs.create(uhc, teamSize));
            registerable.register(UHCGameConfigs.of("uhcrun/" + teamSize.getName()), UHCGameConfigs.create(uhcRun, teamSize));
            registerable.register(UHCGameConfigs.of("doublerunner/" + teamSize.getName()), UHCGameConfigs.create(doublerunner, teamSize));
        }
    }
}
