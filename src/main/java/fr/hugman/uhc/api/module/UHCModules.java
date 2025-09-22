package fr.hugman.uhc.api.module;

import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UHCModules {
    // UHCRun
    public static final RegistryKey<UHCModule> ANIMAL_COOKED_FOOD = of("animal_cooked_food");
    public static final RegistryKey<UHCModule> BETTER_TOOLS = of("better_tools");
    public static final RegistryKey<UHCModule> BLASTED_ORES = of("blasted_ores");
    public static final RegistryKey<UHCModule> DASHER = of("dasher");
    public static final RegistryKey<UHCModule> FASTER_RESOURCES = of("faster_resources");
    public static final RegistryKey<UHCModule> GUARANTEED_APPLES = of("guaranteed_apples");
    public static final RegistryKey<UHCModule> MOB_COOKED_FOOD = of("mob_cooked_food");
    public static final RegistryKey<UHCModule> ORE_BOOST = of("ore_boost");
    public static final RegistryKey<UHCModule> TIMBERMAN = of("timberman");

    // DoubleRunner
    public static final RegistryKey<UHCModule> GUARANTEED_GOLDEN_APPLES = of("guaranteed_golden_apples");
    public static final RegistryKey<UHCModule> DASHER_PLUS = of("dasher_plus");
    public static final RegistryKey<UHCModule> ORE_BOOST_PLUS = of("ore_boost_plus");
    public static final RegistryKey<UHCModule> BLASTED_ORES_PLUS = of("blasted_ores_plus");
    public static final RegistryKey<UHCModule> POTION_DROPS = of("potion_drops");
    public static final RegistryKey<UHCModule> FASTER_RESOURCES_PLUS = of("faster_resources_plus");
    public static final RegistryKey<UHCModule> BETTER_TOOLS_PLUS = of("better_tools_plus");

    public static RegistryKey<UHCModule> of(String path) {
        return RegistryKey.of(UHCRegistryKeys.UHC_MODULE, UHC.id(path));
    }

    public static UHCModule create(
            RegistryKey<UHCModule> key,
            ItemConvertible icon,
            Modifier... modifiers
    ) {
        return create(key, b -> b.icon(icon).modifiers(modifiers));
    }

    public static UHCModule create(
            RegistryKey<UHCModule> key,
            Function<UHCModule.Builder, UHCModule.Builder> builderFunction,
            String... longDescriptionStrings
    ) {
        var translationKey = Util.createTranslationKey("module", key.getValue());
        var builder = UHCModule.builder()
                .nameFrom(key)
                .descriptionFrom(key);

        if (longDescriptionStrings.length > 0) {
            builder.longDescription(Arrays.stream(longDescriptionStrings)
                    .map(s -> Text.translatable(translationKey + ".description." + s))
                    .collect(Collectors.toUnmodifiableList())
            );
        }

        return builderFunction.apply(builder).build();
    }
}
