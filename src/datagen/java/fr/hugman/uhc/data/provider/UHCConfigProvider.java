package fr.hugman.uhc.data.provider;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCMapConfig;
import fr.hugman.uhc.api.config.UHCTimersConfig;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.util.DoubleRange;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.concurrent.CompletableFuture;

public class UHCConfigProvider extends FabricDynamicRegistryProvider {
    public UHCConfigProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(UHCRegistryKeys.UHC_CONFIG));
    }

    @Override
    public String getName() {
        return "UHC Configs";
    }

    public static void register(Registerable<UHCConfig> registerable) {
        var modules = registerable.getRegistryLookup(UHCRegistryKeys.UHC_MODULE);

        registerable.register(UHCConfigs.STANDARD_UHC, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(400, 10000),
                0.5D
        )));
        registerable.register(UHCConfigs.STANDARD_UHCRUN, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(200, 8000),
                0.6D
        ), UHCTimersConfig.DEFAULT.withWarmup(1200), modules.getOrThrow(UHCModuleTags.UHCRUN)));
        registerable.register(UHCConfigs.STANDARD_DOUBLERUNNER, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(200, 8000),
                0.75D
        ), UHCTimersConfig.DEFAULT.withWarmup(600), modules.getOrThrow(UHCModuleTags.DOUBLERUNNER)));
    }
}
