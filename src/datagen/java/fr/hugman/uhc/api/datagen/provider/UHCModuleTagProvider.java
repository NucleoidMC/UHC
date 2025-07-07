package fr.hugman.uhc.api.datagen.provider;

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
                .add(DASHER)
                .add(BETTER_TOOLS)
                .addOptional(BLASTED_ORES)
                .add(ORE_BOOST)
                .addOptional(TIMBERMAN)
                .addOptional(GUARANTEED_APPLES)
                .add(ANIMAL_COOKED_FOOD)
                .addOptional(MOB_COOKED_FOOD)
                .addOptional(FASTER_RESOURCES)
        ;
        builder(UHCModuleTags.DOUBLERUNNER)
                .add(DASHER_PLUS)
                .add(BETTER_TOOLS_PLUS)
                .addOptional(BLASTED_ORES_PLUS)
                .add(ORE_BOOST_PLUS)
                .addOptional(TIMBERMAN)
                .addOptional(GUARANTEED_GOLDEN_APPLES)
                .add(ANIMAL_COOKED_FOOD)
                .addOptional(MOB_COOKED_FOOD)
                .addOptional(FASTER_RESOURCES_PLUS)
                .addOptional(POTION_DROPS);
    }
}