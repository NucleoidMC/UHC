package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.api.module.UHCModule;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;

public class UHCRegistries {
    public static final MappedRegistry<ModifierType<?>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(UHCRegistryKeys.MODIFIER_TYPE).buildAndRegister();

    public static void register() {
        DynamicRegistries.register(UHCRegistryKeys.UHC_MODULE, UHCModule.CODEC);
        DynamicRegistries.register(UHCRegistryKeys.UHC_CONFIG, UHCConfig.CODEC);
    }
}
