package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.PermanentEffectModulePiece;
import com.hugman.uhc.module.piece.PlayerAttributeModulePiece;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class UHCParticipant {
	private final ServerPlayerEntity player;
	private int kills = 0;

	public UHCParticipant(ServerPlayerEntity player) {
		this.player = player;
	}

	public ServerPlayerEntity getPlayer() {
		return player;
	}

	public void addKill() {
		this.kills++;
	}

	public int getKills() {
		return this.kills;
	}

	public void setAdventure() {
		this.getPlayer().setGameMode(GameMode.ADVENTURE);
	}

	public void setSurvival() {
		this.getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	public void setSpectator() {
		this.getPlayer().setGameMode(GameMode.SPECTATOR);
		this.reset();
	}

	public String getName() {
		return this.getPlayer().getEntityName();
	}

	public void reset() {
		this.clear();
		player.inventory.clear();
		player.getEnderChestInventory().clear();
		player.clearStatusEffects();
		player.getHungerManager().setFoodLevel(20);
		player.setExperienceLevel(0);
		player.setExperiencePoints(0);
		player.setHealth(player.getMaxHealth());
	}

	public void clear() {
		player.extinguish();
		player.fallDistance = 0.0F;
	}

	public void refreshAttributes(UHCConfig config) {
		for(PlayerAttributeModulePiece piece : config.playerAttributeModulePieces) {
			piece.setAttribute(player);
		}
	}

	public void applyEffects(UHCConfig config, int effectDuration) {
		for(PermanentEffectModulePiece piece : config.permanentEffectModulePieces) {
			piece.setEffect(player, effectDuration);
		}
	}
}
