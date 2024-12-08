package com.hugman.uhc.game;

import com.hugman.uhc.config.UHCGameConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.nucleoid.plasmid.api.game.GameActivity;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.team.*;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UHCPlayerManager {
    private final Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants;
    private final TeamManager teamManager;
    private final List<GameTeam> aliveTeams;

    public UHCPlayerManager(Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants, TeamManager teamManager, List<GameTeam> aliveTeams) {
        this.participants = participants;
        this.teamManager = teamManager;
        this.aliveTeams = aliveTeams;
    }

    public static UHCPlayerManager of(GameActivity activity, GameSpace gameSpace, UHCGameConfig config) {
        Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants = new Object2ObjectOpenHashMap<>();
        TeamManager teamManager = TeamManager.addTo(activity);
        List<GameTeam> teamsAlive = new ArrayList<>();

        List<DyeColor> teamColors = Arrays.stream(DyeColor.values()).collect(Collectors.toList());
        teamColors.remove(DyeColor.WHITE);
        teamColors.remove(DyeColor.BLACK);
        teamColors.remove(DyeColor.MAGENTA);
        Collections.shuffle(teamColors);

        for (int i = 0; i < Math.round(gameSpace.getPlayers().size() / (float) config.teamSize()); i++) {
            GameTeam team = new GameTeam(new GameTeamKey(RandomStringUtils.randomAlphabetic(16)),
                    GameTeamConfig.builder()
                            .setFriendlyFire(false)
                            .setCollision(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)
                            .setName(Text.literal("UHC Team")) //TODO: Add correct team name
                            .setColors(GameTeamConfig.Colors.from(teamColors.get(i)))
                            .build());

            teamManager.addTeam(team);
            teamsAlive.add(team);
        }


        TeamAllocator<GameTeam, ServerPlayerEntity> allocator = new TeamAllocator<>(teamsAlive);
        for (ServerPlayerEntity playerEntity : gameSpace.getPlayers()) {
            allocator.add(playerEntity, null);
        }
        allocator.allocate((team, player) -> {
            participants.put(player, new UHCParticipant());
            teamManager.addPlayerTo(player, team.key());
        });

        TeamChat.addTo(activity, teamManager);

        return new UHCPlayerManager(participants, teamManager, teamsAlive);
    }

    public void clear() {
        participants.clear();
    }

    // PARTICIPANTS

    public UHCParticipant getParticipant(ServerPlayerEntity player) {
        return participants.get(player);
    }

    public boolean isParticipant(ServerPlayerEntity player) {
        return participants.containsKey(player);
    }

    public int participantCount() {
        return participants.size();
    }

    public int aliveParticipantCount() {
        return (int) participants.values().stream().filter(participant -> !participant.isEliminated()).count();
    }

    public void forEachAliveParticipant(final Consumer<ServerPlayerEntity> consumer) {
        participants.forEach((player, participant) -> {
            if (!participant.isEliminated()) {
                consumer.accept(player);
            }
        });
    }

    // TEAMS

    public PlayerSet teamPlayers(GameTeamKey team) {
        return teamManager.playersIn(team);
    }

    public int aliveTeamCount() {
        return aliveTeams.size();
    }

    public boolean noTeamsAlive() {
        return aliveTeams.isEmpty();
    }

    public void refreshAliveTeams() {
        aliveTeams.removeIf(team -> teamManager.playersIn(team.key()).stream().allMatch(playerEntity -> getParticipant(playerEntity).isEliminated()));
    }

    public GameTeam getLastTeam() {
        return aliveTeams.getFirst();
    }

    public List<GameTeam> aliveTeams() {
        return aliveTeams;
    }
}
