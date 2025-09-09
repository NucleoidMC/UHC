package fr.hugman.ultimate_lucky_block.api.datagen.provider;

import fr.hugman.ultimate_lucky_block.api.module.ULBUHCModules;
import fr.hugman.ultimate_lucky_block.api.registry.ULBUHCModuleTags;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBUHCModuleTagProvider extends FabricTagProvider<UHCModule> {
    public ULBUHCModuleTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, UHCRegistryKeys.UHC_MODULE, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(ULBUHCModuleTags.UHCRUN)
                .forceAddTag(UHCModuleTags.UHCRUN)
                .add(ULBUHCModules.LUCKY_BLOCKS);
        builder(ULBUHCModuleTags.DOUBLERUNNER)
                .forceAddTag(UHCModuleTags.DOUBLERUNNER)
                .add(ULBUHCModules.LUCKY_BLOCKS);
    }

    @Override
    public String getName() {
        return super.getName() + " (Lucky)";
    }
}