package fr.hugman.lucky_block.data;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.data.provider.LuckyBlockConfiguredFeatureProvider;
import fr.hugman.lucky_block.data.provider.LuckyBlockEventProvider;
import fr.hugman.lucky_block.data.provider.LuckyBlockPlacedFeatureProvider;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.uhc.UHC;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

public class LuckyBlockDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // - Lucky Block
        pack.addProvider(LuckyBlockEventProvider::new);

        // - World Generation
        pack.addProvider(LuckyBlockConfiguredFeatureProvider::new);
        pack.addProvider(LuckyBlockPlacedFeatureProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        // - Lucky Block
        registryBuilder.addRegistry(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockEventProvider::register);

        // - World Generation
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, LuckyBlockConfiguredFeatureProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, LuckyBlockPlacedFeatureProvider::register);
    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return LuckyBlockMod.MOD_ID;
    }
}
