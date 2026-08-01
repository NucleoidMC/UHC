package fr.hugman.uhc.api.gui.creator;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import fr.hugman.uhc.api.gui.widget.ListGuiWidget;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Offers a starting point for a custom game. The three standard modes get fixed slots so they are always in the same
 * place, and anything else the data packs define sits one click away.
 */
public class SelectPresetGui extends PreviousableGui {
    private static final int HEIGHT = 3;

    private static final List<ResourceKey<UHCConfig>> FEATURED = List.of(
            UHCConfigs.STANDARD_UHC, UHCConfigs.STANDARD_UHCRUN, UHCConfigs.STANDARD_DOUBLERUNNER);

    public SelectPresetGui(ServerPlayer player) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setTitle(Component.translatable("ui.uhc.select_preset.title"));
        addBackButton();

        int middleRow = (HEIGHT - 1) * 9 / 2;
        var registry = player.level().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_CONFIG);

        int slot = middleRow + 2;
        for (var key : FEATURED) {
            // A data pack is free to drop a standard config; its slot simply stays empty rather than breaking the menu.
            var preset = registry.get(key);
            if (preset.isPresent()) {
                setSlot(slot, select(preset.get()));
            }
            slot++;
        }

        if (!others(player).isEmpty()) {
            setSlot(middleRow + 6, UHCConfigGuiElements.more(player)
                    .setCallback(() -> {
                        UHCConfigGuiElements.playClickSound(player);
                        new MorePresetsGui(player).open();
                    }));
        }
    }

    private GuiElementBuilder select(Holder<UHCConfig> preset) {
        return UHCConfigGuiElements.preset(player, preset).setCallback(() -> {
            UHCConfigGuiElements.playClickSound(player);
            edit(player, preset);
        });
    }

    static void edit(ServerPlayer player, Holder<UHCConfig> preset) {
        new CreateUHCGui(player, preset, preset.value().clone()).open();
    }

    /**
     * Every configuration that does not already have a fixed slot.
     */
    static List<Holder<UHCConfig>> others(ServerPlayer player) {
        return player.level().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_CONFIG)
                .listElements()
                .map(entry -> (Holder<UHCConfig>) entry)
                .filter(entry -> entry.unwrapKey().map(key -> !FEATURED.contains(key)).orElse(true))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * The overflow list, for everything the data packs add beyond the standard modes.
     */
    public static class MorePresetsGui extends PreviousableGui {
        private static final int LIST_HEIGHT = 4;

        private final List<Holder<UHCConfig>> presets;

        public MorePresetsGui(ServerPlayer player) {
            super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + LIST_HEIGHT)), player, false);
            this.presets = others(player);
        }

        @Override
        public void onOpen() {
            super.onOpen();
            setTitle(Component.translatable("ui.uhc.more"));
            addBackButton();

            var widget = new ListGuiWidget<>(this, player, presets,
                    preset -> UHCConfigGuiElements.preset(player, preset),
                    1, LIST_HEIGHT - 1,
                    preset -> edit(player, preset));
            widget.refreshDisplay();
        }
    }
}
