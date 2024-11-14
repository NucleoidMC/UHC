package com.hugman.uhc.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public record PlacedFeaturesModifier(RegistryEntryList<PlacedFeature> features) implements Modifier {
    public static final MapCodec<PlacedFeaturesModifier> CODEC = PlacedFeature.LIST_CODEC.optionalFieldOf("features", RegistryEntryList.of()).xmap(PlacedFeaturesModifier::new, PlacedFeaturesModifier::features);

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLACED_FEATURES;
    }

    public void generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
        this.features().forEach(entry -> entry.value().generate(world, generator, random, pos));
    }
}
