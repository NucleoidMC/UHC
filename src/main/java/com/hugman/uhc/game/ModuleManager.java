package com.hugman.uhc.game;

import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;

public record ModuleManager(
        RegistryEntryList<Module> modules
) {
    public List<Modifier> getModifiers() {
        List<Modifier> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            modifiers.addAll(moduleEntry.value().modifiers());
        }
        return modifiers;
    }

    public <V extends Modifier> List<V> getModifiers(ModifierType<V> type) {
        //TODO: cache modules so it's quicker to sort by type
        return filter(modules, type);
    }

    public static <V extends Modifier> List<V> filter(RegistryEntryList<Module> modules, ModifierType<V> type) {
        List<V> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            for (Modifier modifier : moduleEntry.value().modifiers()) {
                if (modifier.getType() == type) {
                    modifiers.add((V) modifier);
                }
            }
        }
        return modifiers;
    }
}
