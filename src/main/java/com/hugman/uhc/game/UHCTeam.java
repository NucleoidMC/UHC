package com.hugman.uhc.game;

import net.minecraft.scoreboard.Team;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.player.GameTeam;

public class UHCTeam {
	private final Team team;
	private final DyeColor color;
	private final Formatting formatting;

	public UHCTeam(Team team, DyeColor color) {
		this.team = team;
		this.color = color;
		this.formatting = formatByDye(color);
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

	private static Formatting formatByDye(DyeColor dye) {
		switch (dye) {
			case WHITE: return Formatting.WHITE;
			case ORANGE: return Formatting.GOLD;
			case MAGENTA: return Formatting.LIGHT_PURPLE;
			case LIGHT_BLUE: return Formatting.AQUA;
			case YELLOW: return Formatting.YELLOW;
			case LIME: return Formatting.GREEN;
			case PINK: return Formatting.LIGHT_PURPLE;
			case GRAY: return Formatting.DARK_GRAY;
			case LIGHT_GRAY: return Formatting.GRAY;
			case CYAN: return Formatting.DARK_AQUA;
			case PURPLE: return Formatting.DARK_PURPLE;
			case BLUE: return Formatting.BLUE;
			case BROWN: return Formatting.DARK_RED;
			case GREEN: return Formatting.DARK_GREEN;
			case RED: return Formatting.RED;
			case BLACK: return Formatting.BLACK;
			default: return Formatting.RESET;
		}
	}
}
