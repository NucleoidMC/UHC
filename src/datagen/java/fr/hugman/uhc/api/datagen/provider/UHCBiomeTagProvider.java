package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.tags.UHCBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class UHCBiomeTagProvider extends FabricTagsProvider<Biome> {
    public UHCBiomeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.BIOME, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(UHCBiomeTags.OCEANLESS_BLACKLIST)
                .addOptionalTag(BiomeTags.IS_OCEAN)
                .addOptionalTag(BiomeTags.IS_RIVER)
                .addOptionalTag(BiomeTags.IS_BEACH)
                .add(Biomes.STONY_SHORE)
        ;
    }
}