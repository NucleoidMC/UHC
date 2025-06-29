package fr.hugman.lucky_block.registry;

import fr.hugman.lucky_block.LuckyBlockMod;
import fr.hugman.lucky_block.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.lucky_event.LuckyEventType;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.modifier.ModifierType;
import fr.hugman.uhc.module.Module;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class LuckyBlockRegistryKeys {
    public static final RegistryKey<Registry<LuckyEvent>> LUCKY_EVENT = RegistryKey.ofRegistry(LuckyBlockMod.id("lucky_event"));
    public static final RegistryKey<Registry<LuckyEventType<?>>> LUCKY_EVENT_TYPE = RegistryKey.ofRegistry(LuckyBlockMod.id("lucky_event_type"));
}
