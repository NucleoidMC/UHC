package fr.hugman.uhc.impl.data;

import fr.hugman.uhc.api.datagen.provider.*;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.impl.UHC;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.registry.PlasmidRegistryKeys;

public class UHCDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // - UHC
        pack.addProvider(UHCConfigProvider::new);
        pack.addProvider(UHCGameProvider::new);
        pack.addProvider(UHCModuleProvider::new);
        pack.addProvider(UHCModuleTagProvider::new);

        // - World Generation
        pack.addProvider(UHCConfiguredFeatureProvider::new);
        pack.addProvider(UHCPlacedFeatureProvider::new);
        pack.addProvider(UHCNoiseSettingsProvider::new);
        pack.addProvider(UHCDensityFunctionProvider::new);

        // Tags
        pack.addProvider(UHCBiomeTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        // - UHC
        registryBuilder.add(PlasmidRegistryKeys.GAME_CONFIG, UHCGameProvider::register);
        registryBuilder.add(UHCRegistryKeys.UHC_CONFIG, UHCConfigProvider::register);
        registryBuilder.add(UHCRegistryKeys.UHC_MODULE, UHCModuleProvider::register);

        // - World Generation
        registryBuilder.add(Registries.CONFIGURED_FEATURE, UHCConfiguredFeatureProvider::register);
        registryBuilder.add(Registries.PLACED_FEATURE, UHCPlacedFeatureProvider::register);
        registryBuilder.add(Registries.NOISE_SETTINGS, UHCNoiseSettingsProvider::register);
        registryBuilder.add(Registries.DENSITY_FUNCTION, UHCDensityFunctionProvider::register);
    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return UHC.MOD_ID;
    }
}
