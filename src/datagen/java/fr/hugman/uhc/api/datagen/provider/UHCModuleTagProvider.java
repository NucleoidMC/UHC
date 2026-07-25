package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static fr.hugman.uhc.api.module.UHCModules.*;

public class UHCModuleTagProvider extends FabricTagsProvider<UHCModule> {
    public UHCModuleTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, UHCRegistryKeys.UHC_MODULE, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(UHCModuleTags.UHCRUN)
                .add(DASHER)
                .add(BETTER_TOOLS)
                .add(BLASTED_ORES)
                .add(ORE_BOOST)
                .add(TIMBERMAN)
                .add(GUARANTEED_APPLES)
                .add(ANIMAL_COOKED_FOOD)
                .add(MOB_COOKED_FOOD)
                .add(FASTER_RESOURCES)
        ;
        builder(UHCModuleTags.DOUBLERUNNER)
                .add(DASHER_PLUS)
                .add(BETTER_TOOLS_PLUS)
                .add(BLASTED_ORES_PLUS)
                .add(ORE_BOOST_PLUS)
                .add(TIMBERMAN)
                .add(GUARANTEED_GOLDEN_APPLES)
                .add(ANIMAL_COOKED_FOOD)
                .add(MOB_COOKED_FOOD)
                .add(FASTER_RESOURCES_PLUS)
                .add(POTION_DROPS);
    }
}
