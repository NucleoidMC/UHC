package com.hugman.uhc.util;

public class TimeFormatter {
	public static String formatTicks(long t) {
		long secondsUntil = t / 20;

		long hours = secondsUntil / 60 / 60;
		long minutes = secondsUntil / 60 % 60;
		long seconds = secondsUntil % 60;
		if(hours > 0) {
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		}
		else {
			return String.format("%02d:%02d", minutes, seconds);
		}
	}
}
