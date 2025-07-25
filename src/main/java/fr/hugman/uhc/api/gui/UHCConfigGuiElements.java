package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.gui.creator.UHCModulesGui;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UHCConfigGuiElements {
    public static GuiElementBuilder back(ServerPlayerEntity player, Runnable runnable) {
        return new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(ScreenTexts.BACK)
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    playClickSound(player);
                    runnable.run();
                });
    }

    public static GuiElementBuilder previousPage(ServerPlayerEntity player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setItemName(Text.translatable("spectatorMenu.previous_page"))
                .hideDefaultTooltip()
                .setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
    }

    public static GuiElementBuilder nextPage(ServerPlayerEntity player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Text.translatable("spectatorMenu.next_page"))
                .hideDefaultTooltip()
                .setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    public static GuiElementBuilder map(ServerPlayerEntity player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.MAP)
                .setName(Text.translatable("text.uhc.map"));

        if (editable) {
            element.addLoreLine(Text.translatable("ui.uhc.click_to_edit").formatted(Formatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                //TODO
            });
        }
        element.addLoreLine(Text.of(""));
        //TODO: Add translations
        element.addLoreLine(Text.of("Dimension: " + config.mapConfig().dimension().getValue()));
        element.addLoreLine(Text.of("Start size: from " + config.mapConfig().startSize().min() + " to " + config.mapConfig().startSize().max() + " blocks"));
        element.addLoreLine(Text.of("End size: from " + config.mapConfig().endSize().min() + " to " + config.mapConfig().endSize().max() + " blocks"));
        element.addLoreLine(Text.of("Shrinking speed: " + config.mapConfig().shrinkingSpeed() + " blocks/second"));
        element.addLoreLine(Text.of("Spawn offset: " + config.mapConfig().spawnOffset() + " blocks"));

        return element;
    }

    public static GuiElementBuilder timers(ServerPlayerEntity player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.CLOCK)
                .hideDefaultTooltip()
                .noDefaults()
                .setName(Text.translatable("text.uhc.timers"));

        if (editable) {
            element.addLoreLine(Text.translatable("ui.uhc.click_to_edit").formatted(Formatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                //TODO
            });
        }
        element.addLoreLine(Text.of(""));
        //TODO: Add translations
        element.addLoreLine(Text.of("1. Cages: " + config.timersConfig().cages() + "s"));
        element.addLoreLine(Text.of("2. Invulnerability: " + config.timersConfig().invulnerability() + "s"));
        element.addLoreLine(Text.of("3. Warmup: " + config.timersConfig().warmup() + "s"));
        element.addLoreLine(Text.of("4. Deathmatch: " + config.timersConfig().deathmatch() + "s"));

        return element;
    }

    public static GuiElementBuilder modules(ServerPlayerEntity player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.KNOWLEDGE_BOOK)
                .hideDefaultTooltip()
                .noDefaults()
                .setName(Text.translatable("text.uhc.modules").append(" ").append(
                        Text.literal("(").append(String.valueOf(config.modules().size())).append(Text.literal(")"))
                ));

        if (editable) {
            element.addLoreLine(Text.translatable("ui.uhc.click_to_edit").formatted(Formatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                var selectedModules = new ArrayList<>(config.modules().stream().toList());
                var others = player.getServer().getRegistryManager().getOrThrow(UHCRegistryKeys.UHC_MODULE)
                        .streamEntries()
                        .map(entry -> (RegistryEntry<UHCModule>) entry)
                        .filter(entry -> !selectedModules.contains(entry))
                        .collect(Collectors.toCollection(ArrayList::new));
                new UHCModulesGui(player, 6, selectedModules, others, config::setModules).open();
            });
        }
        return element;
    }

    public static GuiElementBuilder launch() {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Text.translatable("ui.uhc.launch"))
                .hideDefaultTooltip()
                .setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    private static void playSound(ServerPlayerEntity player, SoundEvent sound) {
        player.playSoundToPlayer(sound, SoundCategory.MASTER, 1, 1);
    }

    public static void playClickSound(ServerPlayerEntity player) {
        playSound(player, SoundEvents.UI_BUTTON_CLICK.value());
    }
}
