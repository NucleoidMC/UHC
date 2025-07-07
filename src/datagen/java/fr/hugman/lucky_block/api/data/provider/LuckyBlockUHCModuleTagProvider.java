package fr.hugman.lucky_block.api.data.provider;

import fr.hugman.lucky_block.api.module.LuckyBlockUHCModules;
import fr.hugman.lucky_block.api.registry.LuckyBlockUHCModuleTags;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCModuleTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LuckyBlockUHCModuleTagProvider extends FabricTagProvider<UHCModule> {
    public LuckyBlockUHCModuleTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, UHCRegistryKeys.UHC_MODULE, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(LuckyBlockUHCModuleTags.UHCRUN)
                .forceAddTag(UHCModuleTags.UHCRUN)
                .add(LuckyBlockUHCModules.LUCKY_BLOCKS);
        builder(LuckyBlockUHCModuleTags.DOUBLERUNNER)
                .forceAddTag(UHCModuleTags.DOUBLERUNNER)
                .add(LuckyBlockUHCModules.LUCKY_BLOCKS);
    }

    @Override
    public String getName() {
        return super.getName() + " (Lucky)";
    }
}