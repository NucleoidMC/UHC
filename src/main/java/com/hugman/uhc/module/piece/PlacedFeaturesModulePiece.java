package com.hugman.uhc.module.piece;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;
import java.util.Objects;

public class PlacedFeaturesModulePiece extends ModulePiece {
	public static final Codec<PlacedFeaturesModulePiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("values").forGetter(module -> module.values)
	).apply(instance, PlacedFeaturesModulePiece::new));

	private final List<Identifier> values;

	private PlacedFeaturesModulePiece(List<Identifier> values) {
		this.values = values;
	}

	@Override
	public ModulePieceType<?> getType() {
		return ModulePieceType.PLACED_FEATURES;
	}

	public List<PlacedFeature> getValues(StructureWorldAccess world) {
		Registry<PlacedFeature> registry = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
		return values.stream().map(registry::get).filter(Objects::nonNull).toList();
	}
}
