package fr.hugman.uhc.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.gen.feature.PlacedFeature;

public record PlacedFeaturesModifier(RegistryEntryList<PlacedFeature> features) implements Modifier {
    public static final MapCodec<PlacedFeaturesModifier> CODEC = PlacedFeature.LIST_CODEC.fieldOf("features").xmap(PlacedFeaturesModifier::new, PlacedFeaturesModifier::features);

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLACED_FEATURES;
    }
}
