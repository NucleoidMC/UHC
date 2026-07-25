package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCMapConfig;
import fr.hugman.uhc.api.config.UHCTimersConfig;
import fr.hugman.uhc.api.datagen.compat.ULBUHCCompat;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.tags.UHCBiomeTags;
import fr.hugman.uhc.api.util.DoubleRange;
import fr.hugman.uhc.api.world.level.levelgen.UHCNoiseSettings;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

import java.util.concurrent.CompletableFuture;

public class UHCConfigProvider extends FabricDynamicRegistryProvider {
    public UHCConfigProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        ULBUHCCompat.addAll(entries, registries.lookupOrThrow(UHCRegistryKeys.UHC_CONFIG));
    }

    @Override
    public String getName() {
        return "UHC Configs";
    }

    public static void register(BootstrapContext<UHCConfig> registerable) {
        var modules = registerable.lookup(UHCRegistryKeys.UHC_MODULE);
        var dimensionsTypes = registerable.lookup(Registries.DIMENSION_TYPE);
        var noiseSettings = registerable.lookup(Registries.NOISE_SETTINGS);
        var biomes = registerable.lookup(Registries.BIOME);

        var multiNoiseBiomeSourceParameterList = registerable.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        var overworldChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromPreset(multiNoiseBiomeSourceParameterList.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)),
                noiseSettings.getOrThrow(UHCNoiseSettings.OVERWORLD_LOWER_SEAS)
        );

        registerable.register(UHCConfigs.STANDARD_UHC, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(400, 10000),
                0.5D
        )));
        registerable.register(UHCConfigs.STANDARD_UHCRUN, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(200, 8000),
                0.6D
        ), UHCTimersConfig.DEFAULT.withWarmup(1200), modules.getOrThrow(UHCModuleTags.UHCRUN)));
        registerable.register(UHCConfigs.STANDARD_DOUBLERUNNER, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(200, 8000),
                0.75D
        ), UHCTimersConfig.DEFAULT.withWarmup(600), modules.getOrThrow(UHCModuleTags.DOUBLERUNNER)));

        registerable.register(UHCConfigs.LUCKY_UHC, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(400, 10000),
                0.5D
        ), UHCTimersConfig.DEFAULT, HolderSet.direct(modules.getOrThrow(UHCModules.LUCKY_BLOCKS))));
        registerable.register(UHCConfigs.LUCKY_UHCRUN, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(200, 8000),
                0.6D
        ), UHCTimersConfig.DEFAULT.withWarmup(1200), modules.getOrThrow(UHCModuleTags.LUCKY_UHCRUN)));
        registerable.register(UHCConfigs.LUCKY_DOUBLERUNNER, new UHCConfig(UHCMapConfig.of(
                dimensionsTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                overworldChunkGenerator,
                biomes.getOrThrow(UHCBiomeTags.OCEANLESS_BLACKLIST),
                new DoubleRange(200, 8000),
                0.75D
        ), UHCTimersConfig.DEFAULT.withWarmup(600), modules.getOrThrow(UHCModuleTags.LUCKY_DOUBLERUNNER)));
    }
}
