package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCGameConfig;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.map.UHCMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public record UHCWaiting(
        GameSpace gameSpace,
        ServerWorld world,
        UHCGameConfig config,
        TeamManager teamManager
) {
    public static GameOpenProcedure open(GameOpenContext<UHCGameConfig> context) {
        UHCMap map = UHCMap.of(context.config());

        return context.openWithWorld(map.createRuntimeWorldConfig(), (activity, world) -> {
            GameWaitingLobby.addTo(activity, context.config().players());
            TeamManager teamManager = TeamManager.addTo(activity);

            UHCWaiting waiting = new UHCWaiting(activity.getGameSpace(), world, context.config(), teamManager);

            activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
            activity.listen(GamePlayerEvents.ACCEPT, waiting::acceptPlayer);
            activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
            activity.listen(PlayerDeathEvent.EVENT, (player, source) -> EventResult.DENY);
            activity.listen(PlayerDamageEvent.EVENT, (player, source, amount) -> EventResult.DENY);
            activity.listen(PlayerAttackEntityEvent.EVENT, (attacker, hand, attacked, hitResult) -> EventResult.DENY);
        });
    }

    private JoinAcceptorResult acceptPlayer(JoinAcceptor joinAcceptor) {
        return joinAcceptor
                .teleport(this.world, UHCSpawner.getSurfaceBlock(world, 0, 0))
                .thenRunForEach(player -> player.changeGameMode(GameMode.ADVENTURE));
    }

    private GameResult requestStart() {
        UHCActive.start(this.gameSpace, this.world, this.config);
        return GameResult.ok();
    }
}
