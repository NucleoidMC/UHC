package fr.hugman.uhc.api.util;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TickUtil {
    public static long asSeconds(long t) {
        return t / 20;
    }

    public static int getSeconds(long t) {
        return (int) asSeconds(t) % 60;
    }

    public static long asMinutes(long t) {
        return asSeconds(t) / 60;
    }

    public static int getMinutes(long t) {
        return (int) asMinutes(t) % 60;
    }

    public static long asHours(long t) {
        return asMinutes(t) / 60;
    }

    public static int getHours(long t) {
        return (int) asHours(t) % 24;
    }

    public static MutableComponent format(long t) {
        if (getHours(t) > 0) {
            return Component.literal(String.format("%02d:%02d:%02d", getHours(t), getMinutes(t), getSeconds(t)));
        } else {
            return Component.literal(String.format("%02d:%02d", getMinutes(t), getSeconds(t)));
        }
    }

    public static MutableComponent formatPretty(long t) {
        MutableComponent text = Component.literal("");
        long hours = getHours(t);
        long minutes = getMinutes(t);
        long seconds = getSeconds(t);

        boolean textBefore = false;
        if (hours > 0) {
            if (hours == 1) {
                text.append(Component.translatable("text.uhc.time.hour"));
            } else {
                text.append(Component.translatable("text.uhc.time.hours", hours));
            }
            textBefore = true;
        }
        if (minutes > 0) {
            if (textBefore)
                text.append(Component.literal(" ")).append(Component.translatable("text.uhc.and")).append(Component.literal(" "));
            if (minutes == 1) {
                text.append(Component.translatable("text.uhc.time.minute"));
            } else {
                text.append(Component.translatable("text.uhc.time.minutes", minutes));
            }
            textBefore = true;
        }
        if (seconds > 0) {
            if (textBefore)
                text.append(Component.literal(" ")).append(Component.translatable("text.uhc.and")).append(Component.literal(" "));
            if (seconds == 1) {
                text.append(Component.translatable("text.uhc.time.second"));
            } else {
                text.append(Component.translatable("text.uhc.time.seconds", seconds));
            }
        }

        return text;
    }
}
