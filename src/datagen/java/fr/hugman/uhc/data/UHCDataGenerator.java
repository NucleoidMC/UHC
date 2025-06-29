package fr.hugman.uhc.data;

import fr.hugman.uhc.UHC;
import fr.hugman.uhc.data.provider.UHCConfiguredFeatureProvider;
import fr.hugman.uhc.data.provider.UHCPlacedFeatureProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

public class UHCDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // - World Generation
        pack.addProvider(UHCConfiguredFeatureProvider::new);
        pack.addProvider(UHCPlacedFeatureProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, UHCConfiguredFeatureProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, UHCPlacedFeatureProvider::register);

    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return UHC.MOD_ID;
    }
}
