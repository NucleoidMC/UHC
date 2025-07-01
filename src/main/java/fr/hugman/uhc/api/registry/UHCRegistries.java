package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.api.module.UHCModule;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

public class UHCRegistries {
    public static final SimpleRegistry<ModifierType<?>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(UHCRegistryKeys.MODIFIER_TYPE).buildAndRegister();

    public static void register() {
        DynamicRegistries.register(UHCRegistryKeys.MODULE, UHCModule.CODEC);
        DynamicRegistries.register(UHCRegistryKeys.UHC_CONFIG, UHCConfig.CODEC);
    }
}
