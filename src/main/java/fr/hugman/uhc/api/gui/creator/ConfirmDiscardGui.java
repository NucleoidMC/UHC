package fr.hugman.uhc.api.gui.creator;

import eu.pb4.sgui.api.gui.SimpleGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Catches a player on their way out of the creator with unsaved changes.
 * <p>
 * This one deliberately does not extend {@link fr.hugman.uhc.api.gui.PreviousableGui}: it is itself the answer to a
 * screen closing, and walking back automatically would undo the very question being asked.
 */
public class ConfirmDiscardGui extends SimpleGui {
    private static final int HEIGHT = 3;

    private final CreateUHCGui creator;
    private final Runnable onDiscard;
    private boolean answered;

    public ConfirmDiscardGui(ServerPlayer player, CreateUHCGui creator, Runnable onDiscard) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
        this.creator = creator;
        this.onDiscard = onDiscard;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setTitle(Component.translatable("ui.uhc.discard.title"));
        int middleRow = (HEIGHT - 1) * 9 / 2;
        setSlot(middleRow + 2, UHCConfigGuiElements.deny(player, "ui.uhc.discard", answer(onDiscard)));
        setSlot(middleRow + 6, UHCConfigGuiElements.confirm(player, "ui.uhc.keep_editing", answer(creator::resume)));
    }

    private Runnable answer(Runnable action) {
        return () -> {
            answered = true;
            action.run();
        };
    }

    /**
     * Escaping out of the question is not an answer, so it is treated as the safe one: keep the changes.
     */
    @Override
    public void afterRemoval() {
        super.afterRemoval();
        if (!answered) {
            creator.resume();
        }
    }
}
