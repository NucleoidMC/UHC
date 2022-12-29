package com.hugman.uhc.util;


import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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

	public static boolean blink(long l, int max, int each) {
		return l <= max && l % each == 0;
	}

	public static MutableText format(long t) {
		if (getHours(t) > 0) {
			return Text.literal(String.format("%02d:%02d:%02d", getHours(t), getMinutes(t), getSeconds(t)));
		} else {
			return Text.literal(String.format("%02d:%02d", getMinutes(t), getSeconds(t)));
		}
	}

	public static MutableText formatPretty(long t) {
		MutableText text = Text.literal("");
		long hours = getHours(t);
		long minutes = getMinutes(t);
		long seconds = getSeconds(t);

		boolean textBefore = false;
		if (hours > 0) {
			if (hours == 1) {
				text.append(Text.translatable("text.uhc.time.hour"));
			} else {
				text.append(Text.translatable("text.uhc.time.hours", hours));
			}
			textBefore = true;
		}
		if (minutes > 0) {
			if (textBefore)
				text.append(Text.literal(" ")).append(Text.translatable("text.uhc.and")).append(Text.literal(" "));
			if (minutes == 1) {
				text.append(Text.translatable("text.uhc.time.minute"));
			} else {
				text.append(Text.translatable("text.uhc.time.minutes", minutes));
			}
			textBefore = true;
		}
		if (seconds > 0) {
			if (textBefore)
				text.append(Text.literal(" ")).append(Text.translatable("text.uhc.and")).append(Text.literal(" "));
			if (seconds == 1) {
				text.append(Text.translatable("text.uhc.time.second"));
			} else {
				text.append(Text.translatable("text.uhc.time.seconds", seconds));
			}
		}

		return text;
	}
}
