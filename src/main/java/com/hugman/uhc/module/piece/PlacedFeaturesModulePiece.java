package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Random;

public record PlacedFeaturesModulePiece(RegistryEntryList<PlacedFeature> features) implements ModulePiece {
	public static final Codec<PlacedFeaturesModulePiece> CODEC = PlacedFeature.LIST_CODEC.optionalFieldOf("features", RegistryEntryList.of()).xmap(PlacedFeaturesModulePiece::new, config -> config.features).codec();

	@Override
	public ModulePieceType<?> getType() {
		return ModulePieceType.PLACED_FEATURES;
	}

	public void generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
		this.features().stream().forEach(entry -> entry.value().generate(world, generator, random, pos));
	}
}
