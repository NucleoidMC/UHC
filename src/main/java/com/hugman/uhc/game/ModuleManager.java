package com.hugman.uhc.game;

import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.module.Module;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ModuleManager {
    private final List<RegistryEntry<Module>> modules;

    public ModuleManager(List<RegistryEntry<Module>> modules) {
        this.modules = new ArrayList<>(modules);
    }

    public ModuleManager(RegistryEntryList<Module> modules) {
        this(modules.stream().toList());
    }

    public boolean isEmpty() {
        return modules.isEmpty();
    }

    public List<Modifier> getModifiers() {
        List<Modifier> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            modifiers.addAll(moduleEntry.value().modifiers());
        }
        return modifiers;
    }

    public <V extends Modifier> List<V> getModifiers(ModifierType<V> type) {
        //TODO: cache modules so it's quicker to sort by type
        return ModuleManager.getModifiers(modules, type);
    }

    public boolean enableModule(RegistryEntry<Module> module) {
        if (modules.contains(module)) {
            return false;
        }
        //TODO: trigger the modifier (they may have something to do when enabled)
        return modules.add(module);
    }

    public boolean disableModule(RegistryEntry<Module> module) {
        if (!modules.contains(module)) {
            return false;
        }

        //TODO: trigger the modifier (they may have something to do when disabled)
        return modules.remove(module);
    }

    /**
     * Builds a GUI for the player to check the list of currently active modules
     *
     * @param player The player to build the GUI for
     * @return The GUI
     */
    public SimpleGui buildGui(ServerPlayerEntity player) {
        ScreenHandlerType<?> type = Registries.SCREEN_HANDLER.get(Identifier.of("generic_9x" + MathHelper.clamp(1, MathHelper.ceil((float) modules.size() / 9), 6)));
        SimpleGui gui = new SimpleGui(type, player, false);
        gui.setTitle(Text.translatable("ui.uhc.modules.title"));
        int i = 0;
        for (var moduleEntry : modules) {
            var module = moduleEntry.value();
            GuiElementBuilder elementBuilder = new GuiElementBuilder(module.icon())
                    .setName(module.name().copy().formatted(Formatting.BOLD).setStyle(Style.EMPTY.withColor(module.color())))
                    .hideDefaultTooltip();
            if (module.longDescription().isPresent()) {
                for (Text line : module.longDescription().get()) {
                    elementBuilder.addLoreLine(Text.literal("- ").append(line).formatted(Formatting.GRAY));
                }
            } else if (module.description().isPresent()) {
                elementBuilder.addLoreLine(Text.literal("- ").append(module.description().get()).formatted(Formatting.GRAY));
            }
            gui.setSlot(i++, elementBuilder);
        }
        return gui;
    }

    public MutableText buildChatMessage() {
        var text = Text.literal("\n").append(Text.translatable("text.uhc.enabled_modules").formatted(Formatting.GOLD));
        this.modules.forEach(moduleEntry -> {
            var module = moduleEntry.value();
            var style = Style.EMPTY;
            if (module.description().isPresent()) {
                style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, module.description().get().copy()));
            }
            text.append(Text.literal("\n  - ").formatted(Formatting.WHITE)).append(Texts.bracketed(module.name()).setStyle(style.withColor(module.color())));
        });
        text.append("\n");
        return text;
    }

    /**
     * Filters a registry entry list of modules by type
     *
     * @return A list of modifiers of the specified type
     */
    public static <V extends Modifier> List<V> getModifiers(List<RegistryEntry<Module>> modules, ModifierType<V> type) {
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


    public static <V extends Modifier> Stream<V> streamModifiers(Stream<RegistryEntry<Module>> modules, ModifierType<V> type) {
        return modules
                .map(RegistryEntry::value)
                .flatMap(module -> module.modifiers().stream())
                .filter(modifier -> modifier.getType() == type)
                .map(modifier -> (V) modifier);
    }

    @Override
    public String toString() {
        return "ModuleManager[" +
                "modules=" + modules + ']';
    }
}
