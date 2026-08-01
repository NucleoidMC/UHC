package fr.hugman.uhc.api.gui.creator;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.SettingsGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import fr.hugman.uhc.api.gui.UHCSettings;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The one page where a custom UHC is put together: everything it holds can be changed here, and launching only asks for
 * a confirmation afterwards.
 */
public class CreateUHCGui extends PreviousableGui {
    private static final int HEIGHT = 3;

    private final Holder<UHCConfig> preset;
    private final UHCConfig config;
    /** What the chosen preset looked like untouched, to tell whether leaving would throw work away. */
    private final UHCConfig baseConfig;
    private UHCGameTeamSize teamSize = UHCGameTeamSize.SOLO;
    /**
     * Set while another screen is being opened on top of this one. sgui reports that as a removal too, and without it
     * every trip to the settings would look like the player walking out.
     */
    private boolean openingChild;

    public CreateUHCGui(ServerPlayer player, Holder<UHCConfig> preset, UHCConfig config) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
        this.preset = preset;
        this.config = config;
        this.baseConfig = config.clone();
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setTitle(Component.translatable("ui.uhc.create_uhc.title"));
        openingChild = false;
        refresh();
    }

    private void refresh() {
        int middleRow = (HEIGHT - 1) * 9 / 2;
        addBackButton();
        setSlot(middleRow + 2, UHCConfigGuiElements.map(player, config, true)
                .setCallback(() -> openChild(new SettingsGui(player,
                        Component.translatable("text.uhc.map"), UHCSettings.map(config), this::refresh))));
        setSlot(middleRow + 3, UHCConfigGuiElements.timers(player, config, true)
                .setCallback(() -> openChild(new SettingsGui(player,
                        Component.translatable("text.uhc.timers"), UHCSettings.timers(config), this::refresh))));
        setSlot(middleRow + 4, UHCConfigGuiElements.modules(player, config, true)
                .setCallback(this::openModules));
        setSlot(middleRow + 6, UHCConfigGuiElements.teamSize(player, teamSize, size -> {
            this.teamSize = size;
            refresh();
        }));
        setSlot(8, UHCConfigGuiElements.launch()
                .setCallback(() -> {
                    UHCConfigGuiElements.playClickSound(player);
                    openChild(new ConfirmLaunchGui(player, preset, config, teamSize));
                }));
    }

    private void openModules() {
        UHCConfigGuiElements.playClickSound(player);
        var selected = new ArrayList<>(config.modules().stream().toList());
        var others = player.level().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_MODULE)
                .listElements()
                .map(entry -> (Holder<UHCModule>) entry)
                .filter(entry -> !selected.contains(entry))
                .collect(Collectors.toCollection(ArrayList::new));
        openChild(new UHCModulesGui(player, 6, selected, others, config::setModules));
    }

    private void openChild(PreviousableGui child) {
        openingChild = true;
        child.open();
    }

    /**
     * Both the back button and the escape key land here, so the prompt guards either way out.
     */
    @Override
    public void afterRemoval() {
        if (openingChild) {
            openingChild = false;
            return;
        }
        if (hasChanges()) {
            new ConfirmDiscardGui(player, this, super::afterRemoval).open();
            return;
        }
        super.afterRemoval();
    }

    /**
     * Reopens the creator with everything still in place, after the player declines to discard.
     */
    public void resume() {
        open();
    }

    private boolean hasChanges() {
        return teamSize != UHCGameTeamSize.SOLO
                || !config.mapConfig().equals(baseConfig.mapConfig())
                || !config.timersConfig().equals(baseConfig.timersConfig())
                || !moduleList(config).equals(moduleList(baseConfig));
    }

    private static List<Holder<UHCModule>> moduleList(UHCConfig config) {
        return config.modules().stream().toList();
    }
}
