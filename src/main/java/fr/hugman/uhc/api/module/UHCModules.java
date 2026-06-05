package fr.hugman.uhc.api.module;

import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;

public class UHCModules {
    // UHCRun
    public static final ResourceKey<UHCModule> ANIMAL_COOKED_FOOD = of("animal_cooked_food");
    public static final ResourceKey<UHCModule> BETTER_TOOLS = of("better_tools");
    public static final ResourceKey<UHCModule> BLASTED_ORES = of("blasted_ores");
    public static final ResourceKey<UHCModule> DASHER = of("dasher");
    public static final ResourceKey<UHCModule> FASTER_RESOURCES = of("faster_resources");
    public static final ResourceKey<UHCModule> GUARANTEED_APPLES = of("guaranteed_apples");
    public static final ResourceKey<UHCModule> MOB_COOKED_FOOD = of("mob_cooked_food");
    public static final ResourceKey<UHCModule> ORE_BOOST = of("ore_boost");
    public static final ResourceKey<UHCModule> TIMBERMAN = of("timberman");

    // DoubleRunner
    public static final ResourceKey<UHCModule> GUARANTEED_GOLDEN_APPLES = of("guaranteed_golden_apples");
    public static final ResourceKey<UHCModule> DASHER_PLUS = of("dasher_plus");
    public static final ResourceKey<UHCModule> ORE_BOOST_PLUS = of("ore_boost_plus");
    public static final ResourceKey<UHCModule> BLASTED_ORES_PLUS = of("blasted_ores_plus");
    public static final ResourceKey<UHCModule> POTION_DROPS = of("potion_drops");
    public static final ResourceKey<UHCModule> FASTER_RESOURCES_PLUS = of("faster_resources_plus");
    public static final ResourceKey<UHCModule> BETTER_TOOLS_PLUS = of("better_tools_plus");

    public static ResourceKey<UHCModule> of(String path) {
        return ResourceKey.create(UHCRegistryKeys.UHC_MODULE, UHC.id(path));
    }

    public static UHCModule create(
            ResourceKey<UHCModule> key,
            ItemLike icon,
            Modifier... modifiers
    ) {
        return create(key, b -> b.icon(icon).modifiers(modifiers));
    }

    public static UHCModule create(
            ResourceKey<UHCModule> key,
            Function<UHCModule.Builder, UHCModule.Builder> builderFunction,
            String... longDescriptionStrings
    ) {
        var translationKey = Util.makeDescriptionId("module", key.location());
        var builder = UHCModule.builder()
                .nameFrom(key)
                .descriptionFrom(key);

        if (longDescriptionStrings.length > 0) {
            builder.longDescription(Arrays.stream(longDescriptionStrings)
                    .map(s -> Component.translatable(translationKey + ".description." + s))
                    .collect(Collectors.toUnmodifiableList())
            );
        }

        return builderFunction.apply(builder).build();
    }
}
