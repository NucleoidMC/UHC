package fr.hugman.uhc.api.gui;

import fr.hugman.uhc.api.util.TickUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * One editable number of a UHC configuration, shown as a single clickable slot.
 * <p>
 * The value is read back through {@link #getter} after every write, so a {@link #setter} is free to clamp against a
 * sibling setting — a minimum that cannot go past its maximum, for instance — and the slot will correctly report that
 * nothing moved.
 *
 * @param key   translation key of the label; {@code key + ".description"} holds the help line
 * @param step  what a plain click adds or removes
 * @param bigStep what a shift-click adds or removes
 */
public record NumberSetting(
        String key,
        ItemLike icon,
        double min,
        double max,
        double step,
        double bigStep,
        Unit unit,
        DoubleSupplier getter,
        DoubleConsumer setter
) {
    public double get() {
        return getter.getAsDouble();
    }

    /**
     * @return whether the value actually moved, so the caller can tell a change from a rejected click
     */
    public boolean adjust(double delta) {
        double before = get();
        // Steps like 0.1 do not survive repeated addition intact, so the result is snapped back onto the step grid.
        double target = Mth.clamp(Math.round((before + delta) * 1000.0) / 1000.0, min, max);
        setter.accept(target);
        return get() != before;
    }

    public Component formatValue() {
        return unit.format(get());
    }

    public Component formatBound(double value) {
        return unit.format(value);
    }

    public enum Unit {
        /** Whole blocks, such as a border size. */
        BLOCKS,
        /** Blocks per second, such as the shrinking speed. */
        BLOCKS_PER_SECOND,
        /** A duration, spelled out as "1 hour and 30 minutes" rather than a raw count. */
        SECONDS;

        public Component format(double value) {
            return switch (this) {
                case BLOCKS -> Component.translatable("ui.uhc.unit.blocks", number(value));
                case BLOCKS_PER_SECOND -> Component.translatable("ui.uhc.unit.blocks_per_second", number(value));
                // formatPretty spells out only the non-zero parts, so zero would come out blank.
                case SECONDS -> value == 0 ? Component.translatable("text.uhc.time.seconds", 0) : TickUtil.formatPretty((long) (value * 20));
            };
        }

        private static String number(double value) {
            return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
        }
    }
}
