package fr.hugman.uhc.api.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Inline 8×8 sprites for text components.
 */
public class Sprites {
    private static final Identifier BLOCK_ATLAS = Identifier.withDefaultNamespace("blocks");
    private static final Identifier ITEM_ATLAS = Identifier.withDefaultNamespace("items");
    private static final Identifier GUI_ATLAS = Identifier.withDefaultNamespace("gui");

    private static final Component ARROW = Component.literal(" → ").withStyle(ChatFormatting.DARK_GRAY);
    /** A sprite advances the cursor by 8 pixels and a space by 4, so two spaces stand in for one missing sprite. */
    private static final String MISSING_SPRITE = "  ";

    public static MutableComponent of(Identifier atlas, Identifier sprite) {
        return Component.object(new AtlasSprite(atlas, sprite));
    }

    /**
     * A block texture, such as {@code cactus_side}. Blocks whose faces differ have no single texture named after them.
     */
    public static MutableComponent block(String texture) {
        return of(BLOCK_ATLAS, Identifier.withDefaultNamespace("block/" + texture));
    }

    /**
     * An item texture, such as {@code cooked_beef}. Block items have none: use {@link #block(String)} for those.
     */
    public static MutableComponent item(String texture) {
        return of(ITEM_ATLAS, Identifier.withDefaultNamespace("item/" + texture));
    }

    /**
     * A status effect icon, such as {@code haste}.
     */
    public static MutableComponent effect(String texture) {
        return of(GUI_ATLAS, Identifier.withDefaultNamespace("mob_effect/" + texture));
    }

    /**
     * Several sprites on a single side of a {@link Transformation}.
     */
    public static List<Component> all(Component... sprites) {
        return List.of(sprites);
    }

    /**
     * What the module hands out outright, with nothing being turned into anything else.
     */
    public static Transformation grant(Component... sprites) {
        return new Transformation(List.of(sprites), List.of());
    }

    public static Transformation transformation(List<Component> from, List<Component> to) {
        return new Transformation(from, to);
    }

    public static Transformation transformation(Component from, Component to) {
        return new Transformation(List.of(from), List.of(to));
    }

    public static Transformation transformation(List<Component> from, Component to) {
        return new Transformation(from, List.of(to));
    }

    public static Transformation transformation(Component from, List<Component> to) {
        return new Transformation(List.of(from), to);
    }

    /**
     * The sprites of one description line, reading as "{@code from} now gives {@code to}".
     * <p>
     * Rendering is deferred so that every line of a module can be padded to a common width: without it, a line with two
     * sprites on a side would push its arrow and its sentence further right than its neighbours.
     */
    public record Transformation(List<Component> from, List<Component> to) {
        public Transformation {
            if (from.isEmpty()) {
                throw new IllegalArgumentException("A transformation needs something to start from");
            }
        }

        /**
         * Renders on its own, with no neighbouring line to line up with.
         */
        public MutableComponent render() {
            return render(from.size(), to.size());
        }

        /**
         * Renders padded to the widest line of the module, in sprites.
         */
        public MutableComponent render(int fromWidth, int toWidth) {
            var component = join(from, fromWidth);
            if (!to.isEmpty()) {
                component.append(ARROW).append(join(to, toWidth));
            }
            return component;
        }

        private static MutableComponent join(List<Component> sprites, int width) {
            var component = sprites.getFirst().copy();
            for (var sprite : sprites.subList(1, sprites.size())) {
                component.append(sprite);
            }
            return component.append(MISSING_SPRITE.repeat(Math.max(0, width - sprites.size())));
        }
    }
}
