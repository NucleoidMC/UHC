package fr.hugman.uhc.impl.game;

import fr.hugman.uhc.api.util.Messenger;
import fr.hugman.uhc.api.util.TickUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.BossBarWidget;

public class UHCBar {
    private final BossBarWidget widget;
    private final Messenger messenger;
    private String symbol;
    private String name;
    private String message;
    private BossEvent.BossBarColor color;
    private long endTick = 0;
    private long totalTicks = 0;
    private boolean canTick = false;

    private UHCBar(BossBarWidget widget, Messenger messenger) {
        this.widget = widget;
        this.messenger = messenger;
    }

    public static UHCBar create(GlobalWidgets widgets, GameSpace gameSpace, Messenger messenger) {
        return new UHCBar(widgets.addBossBar(gameSpace.getMetadata().sourceConfig().value().name(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS), messenger);
    }

    public void set(String symbol, String name, long totalTicks, long endTick, BossEvent.BossBarColor color) {
        this.symbol = symbol;
        this.name = name + ".countdown_bar";
        this.message = name + ".countdown_text";
        this.totalTicks = totalTicks;
        this.endTick = endTick;
        this.color = color;
        this.canTick = true;
    }

    public void set(String name, long totalTicks, long endTick, BossEvent.BossBarColor color) {
        this.set(null, name, totalTicks, endTick, color);
    }

    public void setFull(Component title) {
        this.widget.setTitle(title);
        this.widget.setStyle(BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
        this.widget.setProgress(1.0f);
        this.canTick = false;
    }

    public void close() {
        this.widget.close();
        this.canTick = false;
    }

    public void tick(ServerLevel world) {
        long ticks = this.endTick - world.getGameTime();
        if (ticks % 20 == 0 && canTick) {
            long seconds = TickUtil.asSeconds(ticks);
            long totalSeconds = TickUtil.asSeconds(totalTicks);

            BossEvent.BossBarColor newColor = this.color;
            if (seconds <= 5 || seconds == 10 || seconds == 15 || seconds == 30 || seconds == 60 || seconds == 150 || seconds == 300 || seconds == 600 || seconds == 900 || seconds == 1800) {
                sendMessage(seconds);
                newColor = BossEvent.BossBarColor.RED;
            }
            this.widget.setTitle(symbol == null ? Component.translatable(name, TickUtil.format(ticks)) : Component.literal(symbol).append(" ").append(Component.translatable(name, TickUtil.format(ticks))));
            this.widget.setStyle(newColor, BossEvent.BossBarOverlay.NOTCHED_10);
            this.widget.setProgress((float) seconds / totalSeconds);
        }
    }

    private void sendMessage(long seconds) {
        float pitch = seconds == 0 ? 1.5F : 1.0F;
        if (this.message != null && seconds != 0) {
            if (symbol != null) {
                messenger.info(symbol, message, TickUtil.formatPretty(seconds * 20).withStyle(ChatFormatting.RED));
            } else {
                messenger.info(message, TickUtil.formatPretty(seconds * 20).withStyle(ChatFormatting.RED));
            }
        }
        messenger.sound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, pitch);
    }
}
