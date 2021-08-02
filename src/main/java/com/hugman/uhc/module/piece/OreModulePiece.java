package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.Random;

public record OreModulePiece(Block block, int count, int size) implements ModulePiece {
	public static final Codec<OreModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.BLOCK.fieldOf("block").forGetter(module -> module.block),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(module -> module.count),
			Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(module -> module.size)
	).apply(instance, OreModulePiece::new));

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public void generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
		Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, this.block.getDefaultState(), this.size)).uniformRange(YOffset.getBottom(), YOffset.fixed(127)).spreadHorizontally().repeat(this.count).generate(world, chunkGenerator, random, pos);
	}
}
