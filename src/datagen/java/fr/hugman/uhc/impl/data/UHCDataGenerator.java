package fr.hugman.uhc.impl.data;

import fr.hugman.lucky_block.api.datagen.provider.*;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.datagen.provider.*;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
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
        pack.addProvider(LuckyBlockModelProvider::new);

        // - Lucky Block
        pack.addProvider(LuckyBlockEventProvider::new);
        pack.addProvider(LuckyBlockPoolEventProvider::new);

        // - UHC
        pack.addProvider(LuckyBlockUHCConfigProvider::new);
        pack.addProvider(LuckyBlockGameProvider::new);
        pack.addProvider(LuckyBlockUHCModuleProvider::new);
        pack.addProvider(LuckyBlockUHCModuleTagProvider::new);

        // - Tags
        pack.addProvider(LuckyBlockEventTagProvider::new);

        // - World Generation
        pack.addProvider(LuckyBlockConfiguredFeatureProvider::new);
        pack.addProvider(LuckyBlockPlacedFeatureProvider::new);

        // - Loot Tables
        pack.addProvider(LuckyBlockLootTableProvider::new);
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

        // - UHC
        registryBuilder.addRegistry(GameConfigs.REGISTRY_KEY, LuckyBlockGameProvider::register);
        registryBuilder.addRegistry(UHCRegistryKeys.UHC_CONFIG, LuckyBlockUHCConfigProvider::register);
        registryBuilder.addRegistry(UHCRegistryKeys.UHC_MODULE, LuckyBlockUHCModuleProvider::register);

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
