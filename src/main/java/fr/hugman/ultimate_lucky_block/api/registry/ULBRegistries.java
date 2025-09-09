package fr.hugman.ultimate_lucky_block.api.registry;

import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.LuckyEventType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class ULBRegistries {
    public static final SimpleRegistry<LuckyEventType<?>> LUCKY_EVENT_TYPE = FabricRegistryBuilder.createSimple(ULBRegistryKeys.LUCKY_EVENT_TYPE).buildAndRegister();

    public static void registerDynamics() {
        DynamicRegistries.register(ULBRegistryKeys.LUCKY_EVENT, LuckyEvent.TYPE_CODEC);
    }
}
