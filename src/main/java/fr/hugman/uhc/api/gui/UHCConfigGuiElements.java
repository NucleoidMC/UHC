package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.gui.creator.UHCModulesGui;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.util.PlayerUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

//TODO: custom icons
public class UHCConfigGuiElements {
    public static GuiElementBuilder back(ServerPlayer player, Runnable runnable) {
        return new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(CommonComponents.GUI_BACK)
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    playClickSound(player);
                    runnable.run();
                });
    }

    public static GuiElementBuilder previousPage(ServerPlayer player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setItemName(Component.translatable("spectatorMenu.previous_page"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
    }

    public static GuiElementBuilder nextPage(ServerPlayer player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("spectatorMenu.next_page"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    public static GuiElementBuilder map(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.MAP)
                .setName(Component.translatable("text.uhc.map"));

        if (editable) {
            element.addLoreLine(Component.translatable("ui.uhc.click_to_edit").withStyle(ChatFormatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                //TODO
            });
        }
        element.addLoreLine(Component.nullToEmpty(""));
        //TODO: Add translations
        config.mapConfig().dimensionType().unwrapKey().ifPresent(dimensionTypeResourceKey -> {
            element.addLoreLine(Component.nullToEmpty("Dimension: " + dimensionTypeResourceKey.identifier()));
        });
        element.addLoreLine(Component.nullToEmpty("Start size: from " + config.mapConfig().startSize().min() + " to " + config.mapConfig().startSize().max() + " blocks"));
        element.addLoreLine(Component.nullToEmpty("End size: from " + config.mapConfig().endSize().min() + " to " + config.mapConfig().endSize().max() + " blocks"));
        element.addLoreLine(Component.nullToEmpty("Shrinking speed: " + config.mapConfig().shrinkingSpeed() + " blocks/second"));
        element.addLoreLine(Component.nullToEmpty("Spawn offset: " + config.mapConfig().spawnOffset() + " blocks"));

        return element;
    }

    public static GuiElementBuilder timers(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.CLOCK)
                .hideDefaultTooltip()
                .setName(Component.translatable("text.uhc.timers"));

        if (editable) {
            element.addLoreLine(Component.translatable("ui.uhc.click_to_edit").withStyle(ChatFormatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                //TODO
            });
        }
        element.addLoreLine(Component.nullToEmpty(""));
        //TODO: Add translations
        element.addLoreLine(Component.nullToEmpty("1. Cages: " + config.timersConfig().cages() + "s"));
        element.addLoreLine(Component.nullToEmpty("2. Invulnerability: " + config.timersConfig().invulnerability() + "s"));
        element.addLoreLine(Component.nullToEmpty("3. Warmup: " + config.timersConfig().warmup() + "s"));
        element.addLoreLine(Component.nullToEmpty("4. Deathmatch: " + config.timersConfig().deathmatch() + "s"));

        return element;
    }

    public static GuiElementBuilder modules(ServerPlayer player) {
        return new GuiElementBuilder()
                .setItem(Items.KNOWLEDGE_BOOK)
                .hideDefaultTooltip()
                .setName(Component.translatable("text.uhc.modules"));
    }

    public static GuiElementBuilder modules(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = modules(player)
                .setName(Component.translatable("text.uhc.modules").append(" ").append(
                        Component.literal("(").append(String.valueOf(config.modules().size())).append(Component.literal(")"))
                ));

        if (editable) {
            element.addLoreLine(Component.translatable("ui.uhc.click_to_edit").withStyle(ChatFormatting.GRAY));

            element.setCallback((index, type, action, gui) -> {
                playClickSound(player);
                var selectedModules = new ArrayList<>(config.modules().stream().toList());
                var others = player.level().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_MODULE)
                        .listElements()
                        .map(entry -> (Holder<UHCModule>) entry)
                        .filter(entry -> !selectedModules.contains(entry))
                        .collect(Collectors.toCollection(ArrayList::new));
                new UHCModulesGui(player, 6, selectedModules, others, config::setModules).open();
            });
        }
        return element;
    }

    public static GuiElementBuilder launch() {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("ui.uhc.launch"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    public static void playClickSound(ServerPlayer player) {
        PlayerUtil.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 1.0F, 1.0F);
    }
}
