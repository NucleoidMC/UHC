package fr.hugman.uhc.api.gui.creator;

import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.widget.ListGuiWidget;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.util.Messenger;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.packrat.Symbol;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class UHCModulesGui extends PreviousableGui {
    private final int height;

    private final boolean editable;
    private final List<RegistryEntry<UHCModule>> selectedModules;
    private final @Nullable List<RegistryEntry<UHCModule>> otherModules;
    private final @Nullable Consumer<List<RegistryEntry<UHCModule>>> closeCallback;

    private ListGuiWidget<RegistryEntry<UHCModule>> selectedModulesWidget;
    private @Nullable ListGuiWidget<RegistryEntry<UHCModule>> otherModulesWidget;

    private UHCModulesGui(ServerPlayerEntity player, int height, boolean editable, List<RegistryEntry<UHCModule>> selectedModules, List<RegistryEntry<UHCModule>> otherModules, @Nullable Consumer<List<RegistryEntry<UHCModule>>> closeCallback) {
        super(Registries.SCREEN_HANDLER.get(Identifier.of("generic_9x" + height)), player, editable);
        this.height = height;
        this.editable = editable;
        this.closeCallback = closeCallback;
        this.selectedModules = selectedModules;
        this.otherModules = otherModules;
    }

    public UHCModulesGui(ServerPlayerEntity player, int height, List<RegistryEntry<UHCModule>> modules) {
        this(player, height, false, modules, null, null);
    }

    public UHCModulesGui(ServerPlayerEntity player, int height, List<RegistryEntry<UHCModule>> selectedModules, List<RegistryEntry<UHCModule>> otherModules, @Nullable Consumer<List<RegistryEntry<UHCModule>>> closeCallback) {
        this(player, height, true, selectedModules, otherModules, closeCallback);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setup();
    }

    @Override
    public void onClose() {
        if (closeCallback != null) {
            closeCallback.accept(selectedModules);
        }
        super.onClose();
    }

    private void setup() {
        boolean hasPreviousUi = previousUi != null;
        boolean editable = this.editable && otherModules != null;
        addBackButton();
        selectedModulesWidget = new ListGuiWidget<>(this, player, selectedModules,
                (moduleEntry) -> {
                    var builder = moduleEntry.value().getElement();
                    if(editable) builder.addLoreLine(Text.translatable("ui.uhc.click_to_remove").formatted(Formatting.GRAY));
                    moduleEntry.value().addDescriptionToElement(builder);
                    return builder;
                },
                hasPreviousUi ? 1 : 0,
                height - (hasPreviousUi ? 1 : 0),
                editable ? (moduleEntry) -> {
                    selectedModules.remove(moduleEntry);
                    otherModules.add(moduleEntry);
                    if (otherModulesWidget != null) {
                        otherModulesWidget.refreshDisplay();
                    }
                } : null);
        selectedModulesWidget.refreshDisplay();
        if (editable) {
            otherModulesWidget = new ListGuiWidget<>(this, player, otherModules,
                    (moduleEntry) -> {
                        var builder = moduleEntry.value().getElement();
                        builder.addLoreLine(Text.translatable("ui.uhc.click_to_add").formatted(Formatting.GRAY));
                        moduleEntry.value().addDescriptionToElement(builder);
                        return builder;
                    },
                    height, 3,
                    (moduleEntry) -> {
                        selectedModules.add(moduleEntry);
                        otherModules.remove(moduleEntry);
                        selectedModulesWidget.refreshDisplay();
                    });
            otherModulesWidget.refreshDisplay();
        }
    }
}
