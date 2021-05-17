package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.PermanentEffectModulePiece;
import com.hugman.uhc.module.piece.PlayerAttributeModulePiece;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class UHCParticipant {
	private int kills = 0;

	public UHCParticipant() {
		this.kills = 0;
	}

	public static void setAdventure(ServerPlayerEntity player) {
		player.setGameMode(GameMode.ADVENTURE);
	}

	public static void setSurvival(ServerPlayerEntity player) {
		player.setGameMode(GameMode.SURVIVAL);
	}

	public static void setSpectator(ServerPlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
		reset(player);
	}

	public static void reset(ServerPlayerEntity player) {
		clear(player);
		player.inventory.clear();
		player.getEnderChestInventory().clear();
		player.clearStatusEffects();
		player.getHungerManager().setFoodLevel(20);
		player.setExperienceLevel(0);
		player.setExperiencePoints(0);
		player.setHealth(player.getMaxHealth());
	}

	public static void clear(ServerPlayerEntity player) {
		player.extinguish();
		player.fallDistance = 0.0F;
	}

	public static void refreshAttributes(ServerPlayerEntity player, UHCConfig config) {
		for(PlayerAttributeModulePiece piece : config.playerAttributeModulePieces) {
			piece.setAttribute(player);
		}
	}

	public static void applyEffects(ServerPlayerEntity player, UHCConfig config, int effectDuration) {
		for(PermanentEffectModulePiece piece : config.permanentEffectModulePieces) {
			piece.setEffect(player, effectDuration);
		}
	}

	public void addKill() {
		this.kills++;
	}

	public int getKills() {
		return this.kills;
	}
}
