package com.hugman.uhc.game.phase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCParticipant;
import com.hugman.uhc.game.map.UHCMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.fantasy.BubbleWorldSpawner;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.AttackEntityListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.TeamAllocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class UHCWaiting {
	public final Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants;
	private final GameSpace gameSpace;
	private final UHCMap map;
	private final UHCConfig config;

	private UHCWaiting(GameSpace gameSpace, UHCMap map, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.participants = new Object2ObjectOpenHashMap<>();
	}

	public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
		UHCMap map = new UHCMap(context.getConfig(), context.getServer());

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
				.setGenerator(map.getChunkGenerator())
				.setSpawner(BubbleWorldSpawner.atSurface(0, 0))
				.setDefaultGameMode(GameMode.ADVENTURE)
				.setGameRule(GameRules.NATURAL_REGENERATION, false)
				.setGameRule(GameRules.DO_MOB_SPAWNING, true)
				.setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
				.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, context.getConfig().getMapConfig().getDimension()));

		return context.createOpenProcedure(worldConfig, game -> {
			UHCWaiting waiting = new UHCWaiting(game.getSpace(), map, context.getConfig());
			GameWaitingLobby.applyTo(game, context.getConfig().getPlayerConfig());

			game.on(PlayerAddListener.EVENT, waiting::addPlayer);
			game.on(PlayerRemoveListener.EVENT, waiting::removePlayer);

			game.on(RequestStartListener.EVENT, waiting::requestStart);
			game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
			game.on(PlayerDamageListener.EVENT, waiting::onPlayerDamaged);
			game.on(AttackEntityListener.EVENT, waiting::onEntityDamaged);
		});
	}

	private void addPlayer(ServerPlayerEntity player) {
		UHCParticipant participant = new UHCParticipant();
		participants.put(player, participant);
	}

	private void removePlayer(ServerPlayerEntity player) {
		participants.remove(player);
	}

	private StartResult requestStart() {
		HashSet<Team> teams = new HashSet<>();
		ServerScoreboard scoreboard = gameSpace.getServer().getScoreboard();

		List<Formatting> teamColors = new ArrayList<>(Arrays.asList(
				Formatting.BLUE,
				Formatting.RED,
				Formatting.YELLOW,
				Formatting.GREEN,
				Formatting.GOLD,
				Formatting.AQUA,
				Formatting.GOLD,
				Formatting.LIGHT_PURPLE,
				Formatting.DARK_PURPLE,
				Formatting.DARK_AQUA,
				Formatting.DARK_RED,
				Formatting.DARK_GREEN,
				Formatting.DARK_BLUE,
				Formatting.DARK_GRAY,
				Formatting.BLACK
		));

		for(int i = 0; i < Math.round(gameSpace.getPlayers().size() / (float) config.getTeamSize()); i++) {
			Team team = scoreboard.addTeam(RandomStringUtils.randomAlphabetic(16));
			team.setFriendlyFireAllowed(false);
			team.setShowFriendlyInvisibles(true);
			team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);
			team.setDisplayName(new LiteralText("Team"));
			if(config.getTeamSize() > 0) team.setColor(teamColors.get(i));

			teams.add(team);
		}

		TeamAllocator<Team, ServerPlayerEntity> allocator = new TeamAllocator<>(teams);

		for(ServerPlayerEntity playerEntity : gameSpace.getPlayers()) {
			allocator.add(playerEntity, null);
		}

		Multimap<Team, ServerPlayerEntity> teamPlayers = HashMultimap.create();
		allocator.allocate((team, player) -> {
			scoreboard.addPlayerToTeam(player.getEntityName(), team);
			teamPlayers.put(team, player);
		});

		UHCActive.start(this.gameSpace, this.config, this.map, this.participants, teamPlayers);
		return StartResult.OK;
	}

	private ActionResult onEntityDamaged(ServerPlayerEntity entity, Hand hand, Entity entity1, EntityHitResult entityHitResult) {
		return ActionResult.FAIL;
	}

	private ActionResult onPlayerDamaged(ServerPlayerEntity entity, DamageSource damageSource, float v) {
		return ActionResult.FAIL;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		return ActionResult.FAIL;
	}
}
