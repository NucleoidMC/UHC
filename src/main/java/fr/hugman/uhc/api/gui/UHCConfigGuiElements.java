package fr.hugman.uhc.api.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.util.TextWrap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.util.PlayerUtil;

import java.util.List;
import java.util.function.Consumer;

//TODO: custom icons
public class UHCConfigGuiElements {
    private static final ChatFormatting TITLE = ChatFormatting.AQUA;

    private static final Component ATTACK_KEY = Component.keybind("key.attack");
    private static final Component USE_KEY = Component.keybind("key.use");
    private static final Component SNEAK_KEY = Component.keybind("key.sneak");

    public static GuiElementBuilder back(ServerPlayer player, Runnable runnable) {
        return new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(CommonComponents.GUI_BACK)
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> {
                    playClickSound(player);
                    runnable.run();
                });
    }

    public static GuiElementBuilder previousPage(ServerPlayer player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setItemName(Component.translatable("spectatorMenu.previous_page"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
    }

    public static GuiElementBuilder nextPage(ServerPlayer player) {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("spectatorMenu.next_page"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    public static GuiElementBuilder map(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.MAP)
                .hideDefaultTooltip()
                .setName(Component.translatable("text.uhc.map").withStyle(TITLE));
        summary(player, element, editable, "text.uhc.map.description", UHCSettings.map(config));
        return element;
    }

    public static GuiElementBuilder timers(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = new GuiElementBuilder()
                .setItem(Items.CLOCK)
                .hideDefaultTooltip()
                .setName(Component.translatable("text.uhc.timers").withStyle(TITLE));
        summary(player, element, editable, "text.uhc.timers.description", UHCSettings.timers(config));
        return element;
    }

    public static GuiElementBuilder modules(ServerPlayer player) {
        return new GuiElementBuilder()
                .setItem(Items.KNOWLEDGE_BOOK)
                .hideDefaultTooltip()
                .setName(Component.translatable("text.uhc.modules"));
    }

    public static GuiElementBuilder modules(ServerPlayer player, UHCConfig config, boolean editable) {
        var element = modules(player)
                .setName(Component.translatable("text.uhc.modules").withStyle(TITLE));
        summary(player, element, editable, "text.uhc.modules.description", List.of());
        element.addLoreLine(Component.translatable("ui.uhc.preset.modules", config.modules().size()).withStyle(ChatFormatting.DARK_GRAY));
        return element;
    }

    /**
     * A single editable number: its help line, where it currently sits, and how clicking moves it.
     *
     * @param onChanged runs only when the value actually moved, so a click stopped by a bound redraws nothing
     */
    public static GuiElementBuilder setting(ServerPlayer player, NumberSetting setting, Runnable onChanged) {
        var element = new GuiElementBuilder(setting.icon().asItem())
                .hideDefaultTooltip()
                .setName(Component.translatable(setting.key()).withStyle(TITLE));

        element.addLoreLine(Component.empty());
        description(player, element, setting.key() + ".description");
        element.addLoreLine(Component.empty());
        element.addLoreLine(Component.translatable("ui.uhc.setting.value",
                setting.formatValue().copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.YELLOW));
        element.addLoreLine(Component.translatable("ui.uhc.setting.range",
                setting.formatBound(setting.min()), setting.formatBound(setting.max())).withStyle(ChatFormatting.DARK_GRAY));
        element.addLoreLine(Component.empty());
        element.addLoreLine(Component.translatable("ui.uhc.setting.adjust",
                ATTACK_KEY, USE_KEY, setting.formatBound(setting.step())).withStyle(ChatFormatting.GRAY));
        element.addLoreLine(Component.translatable("ui.uhc.setting.adjust_big",
                SNEAK_KEY, setting.formatBound(setting.bigStep())).withStyle(ChatFormatting.GRAY));

        element.setCallback((index, type, action, gui) -> {
            if (!type.isLeft && !type.isRight) {
                return;
            }
            double amount = type.shift ? setting.bigStep() : setting.step();
            if (setting.adjust(type.isRight ? -amount : amount)) {
                playClickSound(player);
                onChanged.run();
            } else {
                playDeniedSound(player);
            }
        });
        return element;
    }

    /**
     * The team size, cycled in place rather than picked on its own screen, so that everything the creator decides stays
     * on one page.
     */
    public static GuiElementBuilder teamSize(ServerPlayer player, UHCGameTeamSize current, Consumer<UHCGameTeamSize> onChange) {
        var element = new GuiElementBuilder(current.getItem())
                .hideDefaultTooltip()
                .setName(Component.translatable("ui.uhc.team_size").withStyle(TITLE));

        element.addLoreLine(Component.translatable("ui.uhc.click_to_cycle", ATTACK_KEY, USE_KEY).withStyle(ChatFormatting.GRAY));
        element.addLoreLine(Component.empty());
        description(player, element, "ui.uhc.team_size.description");
        element.addLoreLine(Component.empty());
        for (var size : UHCGameTeamSize.values()) {
            boolean selected = size == current;
            element.addLoreLine(Component.translatable(selected ? "ui.uhc.option.selected" : "ui.uhc.option", size.getDisplayName())
                    .withStyle(selected ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY));
        }
        element.addLoreLine(Component.empty());
        element.addLoreLine(Component.translatable("ui.uhc.min_players", current.getMinPlayers()).withStyle(ChatFormatting.DARK_GRAY));

        element.setCallback((index, type, action, gui) -> {
            if (!type.isLeft && !type.isRight) {
                return;
            }
            playClickSound(player);
            onChange.accept(current.next(type.isLeft));
        });
        return element;
    }

    /**
     * A configuration offered as a starting point. Configurations added by a data pack without a name fall back to
     * their id, which is at least enough to tell them apart.
     */
    public static GuiElementBuilder preset(ServerPlayer player, Holder<UHCConfig> preset) {
        var config = preset.value();
        var element = new GuiElementBuilder(config.icon().orElseGet(() -> new ItemStackTemplate(Items.COMPASS)))
                .hideDefaultTooltip()
                .setName(presetName(preset).copy().withStyle(ChatFormatting.BOLD, TITLE));

        element.addLoreLine(Component.translatable("ui.uhc.click_to_select").withStyle(ChatFormatting.GRAY));
        element.addLoreLine(Component.empty());
        element.addLoreLine(Component.translatable("ui.uhc.preset.modules", config.modules().size()).withStyle(ChatFormatting.DARK_GRAY));
        return element;
    }

    public static Component presetName(Holder<UHCConfig> preset) {
        return preset.value().name()
                .orElseGet(() -> Component.literal(preset.unwrapKey().map(key -> key.identifier().toString()).orElse("?")));
    }

    public static GuiElementBuilder more(ServerPlayer player) {
        var element = new GuiElementBuilder(Items.BUNDLE)
                .hideDefaultTooltip()
                .setName(Component.translatable("ui.uhc.more").withStyle(TITLE));
        element.addLoreLine(Component.translatable("ui.uhc.click_to_select").withStyle(ChatFormatting.GRAY));
        element.addLoreLine(Component.empty());
        description(player, element, "ui.uhc.more.description");
        return element;
    }

    public static GuiElementBuilder confirm(ServerPlayer player, String key, Runnable onConfirm) {
        var element = new GuiElementBuilder(Items.CONCRETE.lime())
                .hideDefaultTooltip()
                .setName(Component.translatable(key).withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN));
        element.addLoreLine(Component.empty());
        description(player, element, key + ".description");
        element.setCallback((index, type, action, gui) -> {
            playClickSound(player);
            onConfirm.run();
        });
        return element;
    }

    public static GuiElementBuilder deny(ServerPlayer player, String key, Runnable onDeny) {
        var element = new GuiElementBuilder(Items.CONCRETE.red())
                .hideDefaultTooltip()
                .setName(Component.translatable(key).withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
        element.addLoreLine(Component.empty());
        description(player, element, key + ".description");
        element.setCallback((index, type, action, gui) -> {
            playClickSound(player);
            onDeny.run();
        });
        return element;
    }

    public static GuiElementBuilder launch() {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
                .setName(Component.translatable("ui.uhc.launch"))
                .hideDefaultTooltip()
                .setProfileSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    static MutableComponent settingSummary(NumberSetting setting) {
        return Component.translatable("ui.uhc.setting.summary",
                Component.translatable(setting.key()), setting.formatValue().copy().withStyle(ChatFormatting.WHITE));
    }

    /**
     * Lays out the shared shape of the creator's entries: how to open it, what it is, then what it holds.
     */
    private static void summary(ServerPlayer player, GuiElementBuilder element, boolean editable, String descriptionKey, List<NumberSetting> values) {
        if (editable) {
            element.addLoreLine(Component.translatable("ui.uhc.click_to_edit").withStyle(ChatFormatting.GRAY));
        }
        element.addLoreLine(Component.empty());
        description(player, element, descriptionKey);
        element.addLoreLine(Component.empty());
        values.forEach(value -> element.addLoreLine(settingSummary(value).withStyle(ChatFormatting.DARK_GRAY)));
    }

    private static void description(ServerPlayer player, GuiElementBuilder element, String key) {
        TextWrap.lines(player, key).forEach(line -> element.addLoreLine(line.copy().withStyle(ChatFormatting.GRAY)));
    }

    public static void playClickSound(ServerPlayer player) {
        PlayerUtil.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 1.0F, 1.0F);
    }

    /**
     * The click sound, dropped low enough to read as a refusal.
     */
    public static void playDeniedSound(ServerPlayer player) {
        PlayerUtil.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 1.0F, 0.5F);
    }
}
