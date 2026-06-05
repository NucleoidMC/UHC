package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCMapConfig;
import fr.hugman.uhc.api.config.UHCTimersConfig;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.util.DoubleRange;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.dimension.LevelStem;
import java.util.concurrent.CompletableFuture;

public class UHCConfigProvider extends FabricDynamicRegistryProvider {
    public UHCConfigProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(UHCRegistryKeys.UHC_CONFIG));
    }

    @Override
    public String getName() {
        return "UHC Configs";
    }

    public static void register(BootstrapContext<UHCConfig> registerable) {
        var modules = registerable.lookup(UHCRegistryKeys.UHC_MODULE);

        registerable.register(UHCConfigs.STANDARD_UHC, new UHCConfig(UHCMapConfig.of(
                LevelStem.OVERWORLD,
                new DoubleRange(400, 10000),
                0.5D
        )));
        registerable.register(UHCConfigs.STANDARD_UHCRUN, new UHCConfig(UHCMapConfig.of(
                LevelStem.OVERWORLD,
                new DoubleRange(200, 8000),
                0.6D
        ), UHCTimersConfig.DEFAULT.withWarmup(1200), modules.getOrThrow(UHCModuleTags.UHCRUN)));
        registerable.register(UHCConfigs.STANDARD_DOUBLERUNNER, new UHCConfig(UHCMapConfig.of(
                LevelStem.OVERWORLD,
                new DoubleRange(200, 8000),
                0.75D
        ), UHCTimersConfig.DEFAULT.withWarmup(600), modules.getOrThrow(UHCModuleTags.DOUBLERUNNER)));
    }
}
