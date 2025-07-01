package fr.hugman.uhc.api.module;

import net.minecraft.registry.entry.RegistryEntry;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public class UHCModuleEvents {
    public static final StimulusEvent<Enable> ENABLE = StimulusEvent.create(Enable.class, (ctx) -> (module) -> {
        try {
            for (Enable listener : ctx.getListeners()) {
                listener.onEnable(module);
            }
        } catch (Throwable throwable) {
            ctx.handleException(throwable);
        }
    });
    public static final StimulusEvent<Disable> DISABLE = StimulusEvent.create(Disable.class, (ctx) -> (module) -> {
        try {
            for (Disable listener : ctx.getListeners()) {
                listener.onDisable(module);
            }
        } catch (Throwable throwable) {
            ctx.handleException(throwable);
        }
    });

    public interface Enable {
        void onEnable(RegistryEntry<UHCModule> moduleEntry);
    }

    public interface Disable {
        void onDisable(RegistryEntry<UHCModule> moduleEntry);
    }
}
