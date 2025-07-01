package fr.hugman.uhc.data.provider;

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
        super(output, UHCRegistryKeys.MODULE, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(UHCModuleTags.UHCRUN).add(
                ANIMAL_COOKED_FOOD, BETTER_TOOLS, BLASTED_ORES, DASHER, FASTER_RESOURCES, GUARANTEED_APPLES,
                MOB_COOKED_FOOD, ORE_BOOST, TIMBERMAN
        );
        builder(UHCModuleTags.DOUBLERUNNER).add(
                TIMBERMAN, GUARANTEED_GOLDEN_APPLES, DASHER_PLUS, ORE_BOOST_PLUS, BLASTED_ORES_PLUS, POTION_DROPS,
                FASTER_RESOURCES_PLUS, BETTER_TOOLS_PLUS, ANIMAL_COOKED_FOOD, MOB_COOKED_FOOD
        );
    }
}