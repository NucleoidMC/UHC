package com.hugman.uhc.game;

import net.minecraft.scoreboard.Team;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

public class UHCTeam {
	private final Team team;
	private final DyeColor color;
	private final Formatting formatting;

	public UHCTeam(Team team, DyeColor color) {
		this.team = team;
		this.color = color;
		this.formatting = formatByDye(color);
	}

	private static Formatting formatByDye(DyeColor dye) {
		return switch(dye) {
			case WHITE -> Formatting.WHITE;
			case ORANGE -> Formatting.GOLD;
			case MAGENTA, PINK -> Formatting.LIGHT_PURPLE;
			case LIGHT_BLUE -> Formatting.AQUA;
			case YELLOW -> Formatting.YELLOW;
			case LIME -> Formatting.GREEN;
			case GRAY -> Formatting.DARK_GRAY;
			case LIGHT_GRAY -> Formatting.GRAY;
			case CYAN -> Formatting.DARK_AQUA;
			case PURPLE -> Formatting.DARK_PURPLE;
			case BLUE -> Formatting.BLUE;
			case BROWN -> Formatting.DARK_RED;
			case GREEN -> Formatting.DARK_GREEN;
			case RED -> Formatting.RED;
			case BLACK -> Formatting.BLACK;
		};
	}

	public Team getTeam() {
		return team;
	}

	public DyeColor getColor() {
		return color;
	}

	public Formatting getFormatting() {
		return formatting;
	}
}
