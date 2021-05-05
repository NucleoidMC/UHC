package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.map.UHCMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.fantasy.BubbleWorldSpawner;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;

public class UHCWaiting {
	private final GameSpace gameSpace;
	private final UHCMap map;
	private final UHCConfig config;

	private UHCWaiting(GameSpace gameSpace, UHCMap map, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
		UHCMap map = new UHCMap(context.getConfig().getMapConfig());

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
				.setGenerator(map.getChunkGenerator(context.getServer()))
				.setSpawner(BubbleWorldSpawner.atSurface(0, 0))
				.setDefaultGameMode(GameMode.ADVENTURE)
				.setGameRule(GameRules.NATURAL_REGENERATION, false)
				.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, context.getConfig().getMapConfig().getDimension()));

		return context.createOpenProcedure(worldConfig, game -> {
			UHCWaiting waiting = new UHCWaiting(game.getSpace(), map, context.getConfig());
			GameWaitingLobby.applyTo(game, context.getConfig().getPlayerConfig());

			game.on(RequestStartListener.EVENT, waiting::requestStart);
			game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
		});
	}

	private StartResult requestStart() {
		UHCActive.open(this.gameSpace, this.map, this.config);
		return StartResult.OK;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		return ActionResult.FAIL;
	}
}
