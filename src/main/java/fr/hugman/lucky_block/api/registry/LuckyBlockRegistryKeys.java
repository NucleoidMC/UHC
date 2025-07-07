package fr.hugman.lucky_block.api.registry;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockRegistryKeys {
    public static final RegistryKey<Registry<LuckyEvent>> LUCKY_EVENT = RegistryKey.ofRegistry(LuckyBlockMod.id("lucky_event"));
    public static final RegistryKey<Registry<LuckyEventType<?>>> LUCKY_EVENT_TYPE = RegistryKey.ofRegistry(LuckyBlockMod.id("lucky_event_type"));
}
