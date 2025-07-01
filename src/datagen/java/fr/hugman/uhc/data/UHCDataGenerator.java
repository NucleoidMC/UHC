package fr.hugman.uhc.data;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.data.provider.LuckyBlockConfiguredFeatureProvider;
import fr.hugman.lucky_block.data.provider.LuckyBlockEventProvider;
import fr.hugman.lucky_block.data.provider.LuckyBlockPlacedFeatureProvider;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.data.provider.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptions;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

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

        //TEMP

        // - Lucky Block
        pack.addProvider(LuckyBlockEventProvider::new);

        // - World Generation
        pack.addProvider(LuckyBlockConfiguredFeatureProvider::new);
        pack.addProvider(LuckyBlockPlacedFeatureProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        // - UHC
        registryBuilder.addRegistry(GameConfigs.REGISTRY_KEY, UHCGameProvider::register);
        registryBuilder.addRegistry(UHCRegistryKeys.UHC_CONFIG, UHCConfigProvider::register);
        registryBuilder.addRegistry(UHCRegistryKeys.UHC_MODULE, UHCModuleProvider::register);

        // - World Generation
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, UHCConfiguredFeatureProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, UHCPlacedFeatureProvider::register);

        //TEMP

        // - Lucky Block
        registryBuilder.addRegistry(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockEventProvider::register);

        // - World Generation
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, LuckyBlockConfiguredFeatureProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, LuckyBlockPlacedFeatureProvider::register);
    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return UHC.MOD_ID;
    }
}
