package fr.hugman.uhc.impl.game.ui.element;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import fr.hugman.uhc.impl.game.ModuleManager;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.plasmid.api.game.common.ui.WaitingLobbyUiElement;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModulesUiElement implements WaitingLobbyUiElement {
    private final ServerPlayer player;

    public ModulesUiElement(ServerPlayer player) {
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
                        player.sendSystemMessage(
                                Component.translatable("text.uhc.modules.no_modules_activated").withStyle(ChatFormatting.RED)
                        );
                        return;
                    }
                    manager.buildGui(player).open();
                })
                .build();
    }
}
