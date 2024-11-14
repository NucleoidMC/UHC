package com.hugman.uhc.registry;

import com.hugman.uhc.modifier.ModifierType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

public class UHCRegistries {
    public static final SimpleRegistry<ModifierType<?>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(UHCRegistryKeys.MODIFIER_TYPE).buildAndRegister();
}
