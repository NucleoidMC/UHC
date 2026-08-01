package fr.hugman.uhc.api.gui;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.util.DoubleRange;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * The editable parts of a {@link UHCConfig}, described once and reused by both the creator's summaries and its
 * editing screens.
 * <p>
 * The dimension type, the chunk generator and the excluded biomes are deliberately left out: they decide what the world
 * itself is, and swapping them mid-creation would mean regenerating everything.
 */
public class UHCSettings {
    public static List<NumberSetting> map(UHCConfig config) {
        return List.of(
                new NumberSetting("ui.uhc.setting.start_size_min", Items.CONCRETE.lime(),
                        100, 20000, 100, 1000, NumberSetting.Unit.BLOCKS,
                        () -> config.mapConfig().startSize().min(),
                        value -> {
                            var size = config.mapConfig().startSize();
                            config.setMapConfig(config.mapConfig().withStartSize(new DoubleRange(Math.min(value, size.max()), size.max())));
                        }),
                new NumberSetting("ui.uhc.setting.start_size_max", Items.CONCRETE.green(),
                        100, 20000, 100, 1000, NumberSetting.Unit.BLOCKS,
                        () -> config.mapConfig().startSize().max(),
                        value -> {
                            var size = config.mapConfig().startSize();
                            config.setMapConfig(config.mapConfig().withStartSize(new DoubleRange(size.min(), Math.max(value, size.min()))));
                        }),
                new NumberSetting("ui.uhc.setting.end_size_min", Items.CONCRETE.orange(),
                        5, 1000, 5, 50, NumberSetting.Unit.BLOCKS,
                        () -> config.mapConfig().endSize().min(),
                        value -> {
                            var size = config.mapConfig().endSize();
                            config.setMapConfig(config.mapConfig().withEndSize(new DoubleRange(Math.min(value, size.max()), size.max())));
                        }),
                new NumberSetting("ui.uhc.setting.end_size_max", Items.CONCRETE.red(),
                        5, 1000, 5, 50, NumberSetting.Unit.BLOCKS,
                        () -> config.mapConfig().endSize().max(),
                        value -> {
                            var size = config.mapConfig().endSize();
                            config.setMapConfig(config.mapConfig().withEndSize(new DoubleRange(size.min(), Math.max(value, size.min()))));
                        }),
                new NumberSetting("ui.uhc.setting.shrinking_speed", Items.SUGAR,
                        0.1, 10, 0.1, 1, NumberSetting.Unit.BLOCKS_PER_SECOND,
                        () -> config.mapConfig().shrinkingSpeed(),
                        value -> config.setMapConfig(config.mapConfig().withShrinkingSpeed(value))),
                new NumberSetting("ui.uhc.setting.spawn_offset", Items.COMPASS,
                        0, 200, 5, 25, NumberSetting.Unit.BLOCKS,
                        () -> config.mapConfig().spawnOffset(),
                        value -> config.setMapConfig(config.mapConfig().withSpawnOffset((int) value)))
        );
    }

    public static List<NumberSetting> timers(UHCConfig config) {
        return List.of(
                new NumberSetting("ui.uhc.setting.cages", Items.IRON_BARS,
                        5, 300, 5, 30, NumberSetting.Unit.SECONDS,
                        () -> config.timersConfig().cages(),
                        value -> config.setTimersConfig(config.timersConfig().withCages(value))),
                new NumberSetting("ui.uhc.setting.invulnerability", Items.SHIELD,
                        0, 600, 10, 60, NumberSetting.Unit.SECONDS,
                        () -> config.timersConfig().invulnerability(),
                        value -> config.setTimersConfig(config.timersConfig().withInvulnerability(value))),
                new NumberSetting("ui.uhc.setting.warmup", Items.GOLDEN_PICKAXE,
                        60, 7200, 30, 300, NumberSetting.Unit.SECONDS,
                        () -> config.timersConfig().warmup(),
                        value -> config.setTimersConfig(config.timersConfig().withWarmup(value))),
                new NumberSetting("ui.uhc.setting.deathmatch", Items.IRON_SWORD,
                        60, 7200, 30, 300, NumberSetting.Unit.SECONDS,
                        () -> config.timersConfig().deathmatch(),
                        value -> config.setTimersConfig(config.timersConfig().withDeathmatch(value)))
        );
    }
}
