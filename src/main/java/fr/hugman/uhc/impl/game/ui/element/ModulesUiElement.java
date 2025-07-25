package fr.hugman.uhc.impl.game.ui.element;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import fr.hugman.uhc.impl.game.ModuleManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.plasmid.api.game.common.ui.WaitingLobbyUiElement;

import java.util.Objects;

public class ModulesUiElement implements WaitingLobbyUiElement {
    private final ServerPlayerEntity player;

    public ModulesUiElement(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public GuiElementInterface createMainElement() {
        return UHCConfigGuiElements.modules(player)
                .setCallback((index, type, action, gui) -> {
                    if (!WaitingLobbyUiElement.isClick(type, gui)) {
                        return;
                    }
                    var manager = Objects.requireNonNull(GameSpaceManager.get().byPlayer(this.player)).getAttachment(ModuleManager.ATTACHMENT);
                    if (manager == null || manager.isEmpty()) {
                        player.sendMessage(
                                Text.translatable("text.uhc.modules.no_modules_activated").formatted(Formatting.RED)
                        );
                        return;
                    }
                    manager.buildGui(player).open();
                })
                .build();
    }
}
