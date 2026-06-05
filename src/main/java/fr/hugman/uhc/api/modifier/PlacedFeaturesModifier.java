package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record PlacedFeaturesModifier(HolderSet<PlacedFeature> features) implements Modifier {
    public static final MapCodec<PlacedFeaturesModifier> CODEC = PlacedFeature.LIST_CODEC.fieldOf("features").xmap(PlacedFeaturesModifier::new, PlacedFeaturesModifier::features);

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PLACED_FEATURES;
    }
}
