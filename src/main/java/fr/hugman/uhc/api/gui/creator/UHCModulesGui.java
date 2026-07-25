package fr.hugman.uhc.api.gui.creator;

import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.widget.ListGuiWidget;
import fr.hugman.uhc.api.module.UHCModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class UHCModulesGui extends PreviousableGui {
    private final int height;

    private final boolean editable;
    private final List<Holder<UHCModule>> selectedModules;
    private final @Nullable List<Holder<UHCModule>> otherModules;
    private final @Nullable Consumer<List<Holder<UHCModule>>> closeCallback;

    private ListGuiWidget<Holder<UHCModule>> selectedModulesWidget;
    private @Nullable ListGuiWidget<Holder<UHCModule>> otherModulesWidget;

    private UHCModulesGui(ServerPlayer player, int height, boolean editable, List<Holder<UHCModule>> selectedModules, List<Holder<UHCModule>> otherModules, @Nullable Consumer<List<Holder<UHCModule>>> closeCallback) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + height)), player, editable);
        this.height = height;
        this.editable = editable;
        this.closeCallback = closeCallback;
        this.selectedModules = selectedModules;
        this.otherModules = otherModules;
    }

    public UHCModulesGui(ServerPlayer player, int height, List<Holder<UHCModule>> modules) {
        this(player, height, false, modules, null, null);
    }

    public UHCModulesGui(ServerPlayer player, int height, List<Holder<UHCModule>> selectedModules, List<Holder<UHCModule>> otherModules, @Nullable Consumer<List<Holder<UHCModule>>> closeCallback) {
        this(player, height, true, selectedModules, otherModules, closeCallback);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setup();
    }

    @Override
    public void afterRemoval() {
        if (closeCallback != null) {
            closeCallback.accept(selectedModules);
        }
        super.afterRemoval();
    }

    private void setup() {
        boolean hasPreviousUi = previousUi != null;
        boolean editable = this.editable && otherModules != null;
        addBackButton();
        selectedModulesWidget = new ListGuiWidget<>(this, player, selectedModules,
                (moduleEntry) -> {
                    var builder = moduleEntry.value().getElement();
                    if (editable)
                        builder.addLoreLine(Component.translatable("ui.uhc.click_to_remove").withStyle(ChatFormatting.GRAY));
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
                        var blocker = UHCModule.findIncompatibility(moduleEntry, selectedModules);
                        if (blocker.isPresent()) {
                            var blockerModule = blocker.get().value();
                            builder.setName(moduleEntry.value().name().copy()
                                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.STRIKETHROUGH));
                            builder.addLoreLine(Component.translatable("ui.uhc.incompatible_with",
                                    blockerModule.name().copy().setStyle(Style.EMPTY.withColor(blockerModule.color()))
                            ).withStyle(ChatFormatting.RED));
                        } else {
                            builder.addLoreLine(Component.translatable("ui.uhc.click_to_add").withStyle(ChatFormatting.GRAY));
                        }
                        moduleEntry.value().addDescriptionToElement(builder);
                        return builder;
                    },
                    height, 3,
                    (moduleEntry) -> {
                        selectedModules.add(moduleEntry);
                        otherModules.remove(moduleEntry);
                        selectedModulesWidget.refreshDisplay();
                    },
                    (moduleEntry) -> UHCModule.findIncompatibility(moduleEntry, selectedModules).isEmpty());
            otherModulesWidget.refreshDisplay();
        }
    }
}
