package fr.hugman.uhc.api.registry;

import fr.hugman.uhc.api.modifier.ModifierType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

public class UHCRegistries {
    public static final SimpleRegistry<ModifierType<?>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(UHCRegistryKeys.MODIFIER_TYPE).buildAndRegister();
}
