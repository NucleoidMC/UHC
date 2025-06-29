package fr.hugman.lucky_block.data;

import fr.hugman.lucky_block.data.provider.LuckyBlockConfiguredFeatureProvider;
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

        // - World Generation
        pack.addProvider(LuckyBlockConfiguredFeatureProvider::new);
        pack.addProvider(LuckyBlockPlacedFeatureProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        // - World Generation
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, LuckyBlockConfiguredFeatureProvider::register);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, LuckyBlockPlacedFeatureProvider::register);
    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        // Temporary namespace, it will be replaced when Lucky Block becomes a standalone mod
        return UHC.MOD_ID;
    }
}
