package fr.hugman.uhc.api.gui;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Shows a row of editable numbers, one slot each. Values are written straight into the configuration being built, so
 * leaving this screen never loses anything — the creator it returns to is what guards against discarding.
 */
public class SettingsGui extends PreviousableGui {
    private static final int HEIGHT = 3;
    /** Where the row of settings starts: second row, one slot in, matching the module lists. */
    private static final int FIRST_SLOT = 9 + 1;

    private final Component title;
    private final List<NumberSetting> settings;
    private final Runnable onEdit;

    public SettingsGui(ServerPlayer player, Component title, List<NumberSetting> settings, Runnable onEdit) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
        this.title = title;
        this.settings = settings;
        this.onEdit = onEdit;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setTitle(title);
        addBackButton();
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < settings.size(); i++) {
            setSlot(FIRST_SLOT + i, UHCConfigGuiElements.setting(player, settings.get(i), () -> {
                onEdit.run();
                refresh();
            }));
        }
    }
}
