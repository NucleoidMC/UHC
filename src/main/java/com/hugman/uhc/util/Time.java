package com.hugman.uhc.util;

public class Time {
	int hours, minutes, seconds;

	public Time(long t) {
		long secondsUntil = t / 20;
		this.hours = (int) (secondsUntil / 60 / 60);
		this.minutes = (int) (secondsUntil / 60 % 60);
		this.seconds = (int) (secondsUntil % 60);
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	@Override
	public String toString() {
		if(hours > 0) {
			return String.format("%02d:%02d:%02d", this.hours, this.minutes, this.seconds);
		}
		else {
			return String.format("%02d:%02d", this.minutes, this.seconds);
		}
	}
}
