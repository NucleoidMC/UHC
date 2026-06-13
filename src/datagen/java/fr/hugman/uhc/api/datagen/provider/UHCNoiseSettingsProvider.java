package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.world.level.levelgen.UHCNoiseSettings;
import fr.hugman.uhc.impl.data.OceanlessOverworldNoiseSettings;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.concurrent.CompletableFuture;

public class UHCNoiseSettingsProvider extends FabricDynamicRegistryProvider {
    public UHCNoiseSettingsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.NOISE_SETTINGS));
    }

    @Override
    public String getName() {
        return "Noise Settings";
    }

    public static void register(BootstrapContext<NoiseGeneratorSettings> registerable) {
        registerable.register(UHCNoiseSettings.OCEANLESS_OVERWORLD, OceanlessOverworldNoiseSettings.get(registerable, false, false));
    }
}
