package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.world.level.levelgen.UHCDensityFunctions;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;

import java.util.concurrent.CompletableFuture;

public class UHCDensityFunctionProvider extends FabricDynamicRegistryProvider {
    public UHCDensityFunctionProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.DENSITY_FUNCTION));
    }

    @Override
    public String getName() {
        return "Density Functions";
    }

    public static void register(BootstrapContext<DensityFunction> registerable) {
        var densityFunctions = registerable.lookup(Registries.DENSITY_FUNCTION);

        registerable.register(UHCDensityFunctions.HIGHER_DEPTH, DensityFunctions.add(DensityFunctions.yClampedGradient(20, 320, 1.5, -1), new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.OFFSET))));
        registerable.register(UHCDensityFunctions.HIGHER_DEPTH_AMPLIFIED, DensityFunctions.add(DensityFunctions.yClampedGradient(20, 320, 1.5, -1), new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.OFFSET_AMPLIFIED))));
        registerable.register(UHCDensityFunctions.HIGHER_DEPTH_LARGE, DensityFunctions.add(DensityFunctions.yClampedGradient(20, 320, 1.5, -1), new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(NoiseRouterData.OFFSET_LARGE))));
    }
}
