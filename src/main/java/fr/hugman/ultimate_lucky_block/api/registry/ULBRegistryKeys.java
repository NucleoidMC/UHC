package fr.hugman.ultimate_lucky_block.api.registry;

import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEventType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBRegistryKeys {
    public static final RegistryKey<Registry<LuckyEvent>> LUCKY_EVENT = RegistryKey.ofRegistry(UltimateLuckyBlock.id("lucky_event"));
    public static final RegistryKey<Registry<LuckyEventType<?>>> LUCKY_EVENT_TYPE = RegistryKey.ofRegistry(UltimateLuckyBlock.id("lucky_event_type"));
}
