package fr.hugman.ultimate_lucky_block.api.datagen.provider;

import fr.hugman.ultimate_lucky_block.api.config.ULBUHCConfigs;
import fr.hugman.ultimate_lucky_block.api.module.ULBUHCModules;
import fr.hugman.ultimate_lucky_block.api.registry.ULBUHCModuleTags;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCMapConfig;
import fr.hugman.uhc.api.config.UHCTimersConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.util.DoubleRange;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBUHCConfigProvider extends FabricDynamicRegistryProvider {
    public ULBUHCConfigProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(UHCRegistryKeys.UHC_CONFIG);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(UltimateLuckyBlock.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "UHC Configs (Lucky)";
    }

    public static void register(Registerable<UHCConfig> registerable) {
        var modules = registerable.getRegistryLookup(UHCRegistryKeys.UHC_MODULE);

        registerable.register(ULBUHCConfigs.LUCKY_UHC, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(400, 10000),
                0.5D
        ), UHCTimersConfig.DEFAULT, RegistryEntryList.of(modules.getOrThrow(ULBUHCModules.LUCKY_BLOCKS))));
        registerable.register(ULBUHCConfigs.LUCKY_UHCRUN, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(200, 8000),
                0.6D
        ), UHCTimersConfig.DEFAULT.withWarmup(1200), modules.getOrThrow(ULBUHCModuleTags.UHCRUN)));
        registerable.register(ULBUHCConfigs.LUCKY_DOUBLERUNNER, new UHCConfig(UHCMapConfig.of(
                DimensionOptions.OVERWORLD,
                new DoubleRange(200, 8000),
                0.75D
        ), UHCTimersConfig.DEFAULT.withWarmup(600), modules.getOrThrow(ULBUHCModuleTags.DOUBLERUNNER)));
    }
}
