package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class UHCEntityTags {
    public static final TagKey<EntityType<?>> DROPS_CHICKEN_FOOD = of("animal_food/chicken");
    public static final TagKey<EntityType<?>> DROPS_BEEF_FOOD = of("animal_food/beef");
    public static final TagKey<EntityType<?>> DROPS_PORKCHOP_FOOD = of("animal_food/porkchop");
    public static final TagKey<EntityType<?>> DROPS_MUTTON_FOOD = of("animal_food/mutton");
    public static final TagKey<EntityType<?>> DROPS_RABBIT_FOOD = of("animal_food/rabbit");
    public static final TagKey<EntityType<?>> DROPS_FISH_FOOD = of("animal_food/fish");

    public static final TagKey<EntityType<?>> DROPS_MOB_BEEF_FOOD = of("mob_food/beef");
    public static final TagKey<EntityType<?>> DROPS_MOB_FISH_FOOD = of("mob_food/fish");

    public static final TagKey<EntityType<?>> DROPS_LEATHER = of("leather");
    public static final TagKey<EntityType<?>> DROPS_STRING = of("string");

    public static final TagKey<EntityType<?>> SPIDERS = of("spiders");
    public static final TagKey<EntityType<?>> SQUIDS = of("squids");

    private static TagKey<EntityType<?>> of(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, UHC.id(path));
    }
}
