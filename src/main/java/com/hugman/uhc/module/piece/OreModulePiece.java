package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;
import java.util.Random;

public class OreModulePiece implements ModulePiece {
	private final Block block;
	private final int count;
	private final int size;

	private final PlacedFeature feature;

	public static final Codec<OreModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.BLOCK.getCodec().fieldOf("block").forGetter(module -> module.block),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(module -> module.count),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(module -> module.size)
	).apply(instance, OreModulePiece::new));

	public OreModulePiece(Block block, int count, int size) {
		this.block = block;
		this.count = count;
		this.size = size;
		this.feature = Feature.ORE.configure(new OreFeatureConfig(OreConfiguredFeatures.BASE_STONE_OVERWORLD, this.block.getDefaultState(), this.size))
				.withPlacement(List.of(
						CountPlacementModifier.of(this.count),
						SquarePlacementModifier.of(),
						HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()),
						BiomePlacementModifier.of()));
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public PlacedFeature getFeature() {
		return feature;
	}
}
