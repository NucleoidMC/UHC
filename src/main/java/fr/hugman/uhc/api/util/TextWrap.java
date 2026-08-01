package fr.hugman.uhc.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xyz.nucleoid.server.translations.api.Localization;

import java.util.ArrayList;
import java.util.List;

/**
 * Breaks long tooltip lines into several shorter ones.
 * <p>
 * Item lore is drawn one component per line and never wraps on its own, so a long sentence runs straight off the side
 * of a small screen. Wrapping has to happen here, which means resolving the translation for the player first.
 */
public class TextWrap {
    /**
     * Roughly how many characters fit before a tooltip starts crowding a 854×480 window at GUI scale 2.
     */
    public static final int TOOLTIP_WIDTH = 40;

    /**
     * Translates {@code key} for {@code player} and splits it on word boundaries.
     *
     * @return the wrapped lines, or the untranslated component as a single line if the server holds no translation for
     * the player's language
     */
    public static List<Component> lines(ServerPlayer player, String key, int width) {
        var resolved = Localization.raw(key, player);
        if (resolved == null) {
            return List.of(Component.translatable(key));
        }

        var lines = new ArrayList<Component>();
        var line = new StringBuilder();
        for (var word : resolved.split(" ")) {
            if (!line.isEmpty() && line.length() + 1 + word.length() > width) {
                lines.add(Component.literal(line.toString()));
                line.setLength(0);
            }
            if (!line.isEmpty()) {
                line.append(' ');
            }
            line.append(word);
        }
        if (!line.isEmpty()) {
            lines.add(Component.literal(line.toString()));
        }
        return lines;
    }

    public static List<Component> lines(ServerPlayer player, String key) {
        return lines(player, key, TOOLTIP_WIDTH);
    }
}
