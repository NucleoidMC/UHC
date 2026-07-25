package fr.hugman.uhc.api.datagen.compat;

import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.config.UHCGameConfigs;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.impl.UHC;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Single entry point for the Ultimate Lucky Block compatibility during data generation.
 * <p>
 * ULB is an <b>optional</b> dependency: everything it provides is still data-generated, but the
 * resulting files are gated behind a {@link ResourceConditions#allModsLoaded(String...) mod loaded}
 * condition so that Fabric silently drops them when ULB is absent instead of failing to resolve
 * {@code ultimate_lucky_block:*} identifiers.
 * <p>
 * Every ULB-dependent object is declared in the static initializer below through {@link #require},
 * and providers apply the conditions by calling {@link #addAll} instead of
 * {@link FabricDynamicRegistryProvider.Entries#addAll}.
 */
public final class ULBUHCCompat {
    public static final String MOD_ID = "ultimate_lucky_block";

    /**
     * The condition every ULB-dependent generated file is gated behind.
     */
    public static final ResourceCondition LOADED = ResourceConditions.allModsLoaded(MOD_ID);

    private static final ResourceCondition[] NO_CONDITIONS = new ResourceCondition[0];
    private static final ResourceCondition[] ULB_CONDITIONS = {LOADED};

    private static final Set<ResourceKey<?>> REQUIRING_ULB = new HashSet<>();

    static {
        require(UHCModules.LUCKY_BLOCKS);
        require(UHCConfigs.LUCKY_UHC, UHCConfigs.LUCKY_UHCRUN, UHCConfigs.LUCKY_DOUBLERUNNER);

        for (UHCGameTeamSize teamSize : UHCGameTeamSize.values()) {
            require(
                    UHCGameConfigs.of("lucky_uhc/" + teamSize.getName()),
                    UHCGameConfigs.of("lucky_uhcrun/" + teamSize.getName()),
                    UHCGameConfigs.of("lucky_doublerunner/" + teamSize.getName())
            );
        }
    }

    private ULBUHCCompat() {
    }

    /**
     * Declares that the given objects may only be loaded when Ultimate Lucky Block is present.
     *
     * @param keys the keys of the objects depending on ULB
     */
    public static void require(ResourceKey<?>... keys) {
        Collections.addAll(REQUIRING_ULB, keys);
    }

    /**
     * @return whether the object was declared as ULB-dependent through {@link #require}
     */
    public static boolean requiresULB(ResourceKey<?> key) {
        return REQUIRING_ULB.contains(key);
    }

    /**
     * @return the conditions to attach to the generated file of the given object, possibly empty
     */
    public static ResourceCondition[] conditionsFor(ResourceKey<?> key) {
        return requiresULB(key) ? ULB_CONDITIONS : NO_CONDITIONS;
    }

    /**
     * Drop-in replacement for {@link FabricDynamicRegistryProvider.Entries#addAll} that attaches the
     * ULB condition to every entry declared through {@link #require}.
     *
     * @return a reference to every added object
     */
    public static <T> List<Holder<T>> addAll(FabricDynamicRegistryProvider.Entries entries, HolderLookup.RegistryLookup<T> registry) {
        return registry.listElementIds()
                .filter(key -> key.identifier().getNamespace().equals(UHC.MOD_ID))
                .map(key -> entries.add(registry, key, conditionsFor(key)))
                .toList();
    }
}
