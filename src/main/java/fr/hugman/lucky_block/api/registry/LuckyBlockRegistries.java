package fr.hugman.lucky_block.api.registry;

import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockRegistries {
    public static final SimpleRegistry<LuckyEventType<?>> LUCKY_EVENT_TYPE = FabricRegistryBuilder.createSimple(LuckyBlockRegistryKeys.LUCKY_EVENT_TYPE).buildAndRegister();

    public static void registerDynamics() {
        DynamicRegistries.register(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyEvent.TYPE_CODEC);
    }
}
