package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;

public record PlacedFeaturesModulePiece(List<Identifier> values) implements ModulePiece {
	public static final Codec<PlacedFeaturesModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("values").forGetter(module -> module.values)
	).apply(instance, PlacedFeaturesModulePiece::new));

	@Override
	public Codec<? extends ModulePiece> getCodec() {
		return CODEC;
	}

	public List<PlacedFeature> getValues(StructureWorldAccess world) {
		var registry = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
		return values.stream().map(registry::get).toList();
	}
}
