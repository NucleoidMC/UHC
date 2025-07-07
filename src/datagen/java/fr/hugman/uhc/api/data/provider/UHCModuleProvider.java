package fr.hugman.uhc.api.data.provider;

import fr.hugman.uhc.api.loot.UHCLootTables;
import fr.hugman.uhc.api.modifier.EntityLootModifier;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModules;
import fr.hugman.uhc.api.registry.UHCEntityTags;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Util;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UHCModuleProvider extends FabricDynamicRegistryProvider {
    public UHCModuleProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(UHCRegistryKeys.UHC_MODULE));
    }

    @Override
    public String getName() {
        return "UHC Modules";
    }

    public static void register(Registerable<UHCModule> registerable) {
        final var entities = registerable.getRegistryLookup(RegistryKeys.ENTITY_TYPE);

        register(registerable, UHCModules.ANIMAL_COOKED_FOOD, Items.COOKED_BEEF,
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_CHICKEN_FOOD), UHCLootTables.COOKED_CHICKEN),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_BEEF_FOOD), UHCLootTables.COOKED_BEEF_1),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_PORKCHOP_FOOD), UHCLootTables.COOKED_PORKCHOP),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_MUTTON_FOOD), UHCLootTables.COOKED_MUTTON),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_RABBIT_FOOD), UHCLootTables.COOKED_RABBIT),
                new EntityLootModifier(entities.getOrThrow(UHCEntityTags.DROPS_FISH_FOOD), UHCLootTables.COOKED_FISH)
        );
    }

    public static void register(
            Registerable<UHCModule> registerable,
            RegistryKey<UHCModule> key,
            ItemConvertible icon,
            Modifier... modifiers
    ) {
        var translationKey = Util.createTranslationKey("module", key.getValue());
        registerable.register(key, new UHCModule(
                Text.translatable(translationKey),
                Optional.of(Text.translatable(translationKey + ".description")),
                Optional.empty(),
                new ItemStack(icon),
                TextColor.fromRgb(3791743),
                List.of(modifiers)
        ));
    }
}
