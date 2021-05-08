package com.hugman.uhc.util;

public final class TimeUtil {
	private TimeUtil() {
	}

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

	public static String format(long t) {
		if(getHours(t) > 0) {
			return String.format("%02d:%02d:%02d", getHours(t), getMinutes(t), getSeconds(t));
		}
		else {
			return String.format("%02d:%02d", getMinutes(t), getSeconds(t));
		}
	}
}
