package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.registry.UHCEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityTypeIds;

import java.util.concurrent.CompletableFuture;

public class UHCEntityTypeTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public UHCEntityTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(UHCEntityTags.SPIDERS)
                .add(EntityTypeIds.SPIDER)
                .add(EntityTypeIds.CAVE_SPIDER)
        ;
        builder(UHCEntityTags.SQUIDS)
                .add(EntityTypeIds.SQUID)
                .add(EntityTypeIds.GLOW_SQUID)
        ;

        builder(UHCEntityTags.DROPS_CHICKEN_FOOD)
                .add(EntityTypeIds.CHICKEN)
        ;
        builder(UHCEntityTags.DROPS_BEEF_FOOD)
                .add(EntityTypeIds.COW)
                .add(EntityTypeIds.HOGLIN)
                .add(EntityTypeIds.MOOSHROOM)
                .add(EntityTypeIds.POLAR_BEAR)
                .add(EntityTypeIds.PANDA)
                .add(EntityTypeIds.HORSE)
                .add(EntityTypeIds.MULE)
        ;
        builder(UHCEntityTags.DROPS_PORKCHOP_FOOD)
                .add(EntityTypeIds.PIG)
                .add(EntityTypeIds.HOGLIN)
        ;
        builder(UHCEntityTags.DROPS_MUTTON_FOOD)
                .add(EntityTypeIds.SHEEP)
        ;
        builder(UHCEntityTags.DROPS_RABBIT_FOOD)
                .add(EntityTypeIds.RABBIT)
                .add(EntityTypeIds.BAT)
        ;
        builder(UHCEntityTags.DROPS_FISH_FOOD)
                .add(EntityTypeIds.COD)
                .add(EntityTypeIds.SALMON)
                .add(EntityTypeIds.TROPICAL_FISH)
                .addTag(UHCEntityTags.SQUIDS)
                .add(EntityTypeIds.DOLPHIN)
        ;

        builder(UHCEntityTags.DROPS_MOB_BEEF_FOOD)
                .add(EntityTypeIds.ZOMBIE)
                .add(EntityTypeIds.ZOMBIE_HORSE)
                .add(EntityTypeIds.ZOMBIE_VILLAGER)
                .add(EntityTypeIds.HUSK)
        ;
        builder(UHCEntityTags.DROPS_MOB_FISH_FOOD)
                .add(EntityTypeIds.GUARDIAN)
        ;

        builder(UHCEntityTags.DROPS_LEATHER)
                .addTag(UHCEntityTags.DROPS_MOB_BEEF_FOOD)
                .addTag(UHCEntityTags.DROPS_BEEF_FOOD)
                .addTag(UHCEntityTags.DROPS_MUTTON_FOOD)
                .addTag(UHCEntityTags.DROPS_PORKCHOP_FOOD)
        ;
        builder(UHCEntityTags.DROPS_STRING)
                .addTag(UHCEntityTags.DROPS_MUTTON_FOOD)
        ;
    }
}
