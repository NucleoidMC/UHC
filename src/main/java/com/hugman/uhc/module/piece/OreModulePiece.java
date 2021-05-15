package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.Random;

public class OreModulePiece implements ModulePiece {
	public static final Codec<OreModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.BLOCK.fieldOf("block").forGetter(module -> module.block),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(module -> module.count),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(module -> module.size)
	).apply(instance, OreModulePiece::new));

	private final Block block;
	private final int count;
	private final int size;

	public OreModulePiece(Block block, int count, int size) {
		this.block = block;
		this.count = count;
		this.size = size;
	}

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public void generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, this.block.getDefaultState(), this.size)).rangeOf(256).spreadHorizontally().repeat(this.count).generate(world, chunkGenerator, random, pos);
	}
}
