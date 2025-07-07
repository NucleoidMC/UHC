package fr.hugman.uhc.api.data.provider;

import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static fr.hugman.uhc.api.module.UHCModules.*;

public class UHCModuleTagProvider extends FabricTagProvider<UHCModule> {
    public UHCModuleTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, UHCRegistryKeys.UHC_MODULE, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(UHCModuleTags.UHCRUN)
                .add(ANIMAL_COOKED_FOOD)
                .addOptional(BETTER_TOOLS)
                .addOptional(BLASTED_ORES)
                .addOptional(DASHER)
                .addOptional(FASTER_RESOURCES)
                .addOptional(GUARANTEED_APPLES)
                .addOptional(MOB_COOKED_FOOD)
                .addOptional(TIMBERMAN)
                .addOptional(ORE_BOOST);
        builder(UHCModuleTags.DOUBLERUNNER)
                .add(ANIMAL_COOKED_FOOD)
                .addOptional(TIMBERMAN)
                .addOptional(GUARANTEED_APPLES)
                .addOptional(DASHER_PLUS)
                .addOptional(ORE_BOOST_PLUS)
                .addOptional(BLASTED_ORES_PLUS)
                .addOptional(POTION_DROPS)
                .addOptional(FASTER_RESOURCES_PLUS)
                .addOptional(BETTER_TOOLS_PLUS)
                .addOptional(MOB_COOKED_FOOD);
    }
}