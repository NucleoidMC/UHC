package fr.hugman.uhc.api.datagen.provider;

import fr.hugman.uhc.api.module.UHCModules;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class UHCEnglishLangProvider extends FabricLanguageProvider {
    public UHCEnglishLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, "en_us", registryLookup);
    }

    @Override
    protected Path getLangFilePath(String code) {
        return this.packOutput
                .createPathProvider(PackOutput.Target.DATA_PACK, "lang")
                .json(Identifier.fromNamespaceAndPath(this.packOutput.getModId(), code));
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Games
        builder.add("game.generic.mode", "%s - %s");
        builder.add("game.uhc", "UHC");
        builder.add("game.uhcrun", "UHCRun");
        builder.add("game.doublerunner", "DoubleRunner");
        builder.add("game.custom_uhc", "Custom UHC");
        builder.add("mode.solo", "Solo");
        builder.add("mode.duos", "Duos");
        builder.add("mode.trios", "Trios");
        builder.add("mode.squads", "Squads");

        // In-game texts
        builder.add("text.uhc.and", "and");
        builder.add("text.uhc.deathmatch", "Deathmatch");
        builder.add("text.uhc.dropped_players", "Players have been dropped on the map.");
        builder.add("text.uhc.dropping.countdown_bar", "Drop in %s");
        builder.add("text.uhc.dropping.countdown_text", "You will be dropped in %s.");
        builder.add("text.uhc.last_one_wins", "Last one standing wins!");
        builder.add("text.uhc.enabled_modules", "Enabled modules:");
        builder.add("text.uhc.no_longer_immune", "You are no longer immune to damages!");
        builder.add("text.uhc.none_win", "No one has won!");
        builder.add("text.uhc.player_eliminated", "%s has been eliminated!");
        builder.add("text.uhc.player_win.solo", "%s has won!");
        builder.add("text.uhc.player_win.team", "%s have won!");
        builder.add("text.uhc.players", "Players: %s");
        builder.add("text.uhc.pvp.countdown_bar", "PvP in %s");
        builder.add("text.uhc.pvp.countdown_text", "PvP will be enabled in %s.");
        builder.add("text.uhc.pvp_enabled", "PvP has been enabled!");
        builder.add("text.uhc.shrinking_finish.countdown_bar", "World stops shrinking in %s");
        builder.add("text.uhc.shrinking_finish.countdown_text", "The world will stop shrinking in %s.");
        builder.add("text.uhc.shrinking_start", "The world has started shrinking!");
        builder.add("text.uhc.shrinking_when_pvp", "The world will start to shrink as soon as PvP gets enabled!");
        builder.add("text.uhc.time", "Time: %s");
        builder.add("text.uhc.time.hour", "1 hour");
        builder.add("text.uhc.time.hours", "%s hours");
        builder.add("text.uhc.time.minute", "1 minute");
        builder.add("text.uhc.time.minutes", "%s minutes");
        builder.add("text.uhc.time.second", "1 second");
        builder.add("text.uhc.time.seconds", "%s seconds");
        builder.add("text.uhc.tp.countdown_bar", "Teleportation in %s");
        builder.add("text.uhc.tp.countdown_text", "You will get teleported in %s.");
        builder.add("text.uhc.vulnerable.countdown_bar", "Damages enabled in %s");
        builder.add("text.uhc.vulnerable.countdown_text", "You will be vulnerable to damage in %s.");
        builder.add("text.uhc.world", "World: %s");
        builder.add("text.uhc.world_will_shrink", "You will get teleported in %s. The world will then start shrinking and PvP will get enabled.");
        builder.add("text.uhc.modules", "Modules");
        builder.add("text.uhc.modules.no_modules_activated", "This game has no active modules!");
        builder.add("text.uhc.module.enabled", "The %s module has been enabled!");
        builder.add("text.uhc.module.disabled", "The %s module has been disabled!");
        builder.add("text.uhc.module.not_found", "Module with id \"%s\" not found");
        builder.add("text.uhc.config.not_found", "UHC configuration with id \"%s\" not found");
        builder.add("text.uhc.timers", "Timers");
        builder.add("text.uhc.map", "Map");
        builder.add("text.uhc.kills", "Kills");

        // Commands
        builder.add("command.modules.no_manager", "The module manager is not available! (please contact the developer)");
        builder.add("command.modules.no_modules_activated", "There are not active modules!");
        builder.add("command.modules.already_enabled", "The %s module is already enabled!");
        builder.add("command.modules.already_disabled", "The %s module is already disabled!");
        builder.add("command.modules.enable.success", "The %s module has been enabled");
        builder.add("command.modules.disable.success", "The %s module has been disabled");

        // Modules
        module(builder, UHCModules.DASHER, "Dasher", "Permanent haste and speed");
        module(builder, UHCModules.DASHER_PLUS, "Dasher+", "Permanent haste and speed, and no fall damage");
        module(builder, UHCModules.BETTER_TOOLS, "Better Tools", "Wooden tools are replaced with stone tools");
        module(builder, UHCModules.BETTER_TOOLS_PLUS, "Better Tools+", "Wooden tools are replaced with iron tools and diamond tools are enchanted with Efficiency III");
        module(builder, UHCModules.BLASTED_ORES, "Blasted Ores", "All ores get automatically smelt, doubled and bring more experience");
        module(builder, UHCModules.BLASTED_ORES_PLUS, "Blasted Ores+");
        module(builder, UHCModules.ORE_BOOST, "Ore Boost", "Lapis, gold and diamond ores are more frequent");
        module(builder, UHCModules.ORE_BOOST_PLUS, "Ore Boost+");
        module(builder, UHCModules.TIMBERMAN, "Timberman", "Trees get cut instantly and drop oak planks");
        module(builder, UHCModules.GUARANTEED_APPLES, "Guaranteed Apples", "All leaves only drop apples");
        module(builder, UHCModules.GUARANTEED_GOLDEN_APPLES, "Guaranteed Golden Apples", "All leaves only drop golden apples");
        module(builder, UHCModules.ANIMAL_COOKED_FOOD, "Animal Cooked Food", "Many animals drop cooked food in larger quantities");
        module(builder, UHCModules.MOB_COOKED_FOOD, "Mob Cooked Food", "Many mobs drop cooked food in larger quantities");
        module(builder, UHCModules.FASTER_RESOURCES, "Need for Resources", "Tweaks lots of drops to ease gathering essential resources");
        module(builder, UHCModules.FASTER_RESOURCES_PLUS, "Need for Resources+");
        module(builder, UHCModules.POTION_DROPS, "Potion Drops", "Some things drop potions");

        // Module long descriptions
        moduleDescription(builder, UHCModules.BETTER_TOOLS_PLUS, "wooden_tools_become_iron", "Wooden tools are replaced with iron tools");
        moduleDescription(builder, UHCModules.BETTER_TOOLS_PLUS, "diamond_tools_get_efficiency", "Diamond tools are enchanted with Efficiency III");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "stones_drop_cobblestone", "All stones types drop cobblestone");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "gravel_drops_arrows_and_flint_and_steel", "Gravel drop arrows and flint and steel");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sand_drops_glass", "Sand drops glass");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sand_drops_glass_bottles", "Sand drops glass bottles");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "cactus_kelp_drop_planks", "Cactus and kelp drop planks");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "dead_bushes_drop_bread", "Dead bushes drop bread");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "mushrooms_drop_stews", "Mushrooms drop stews");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "animals_and_zombies_drop_leather", "Most animals and zombies drop leather");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "animals_and_zombies_drop_books", "Most animals and zombies drop books");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sheep_drop_strings", "Sheep drop strings");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sheep_drop_bows", "Sheep drop bows");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sugar_cane_drop_paper", "Sugar cane drop paper");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "sugar_cane_drop_enchanting_tables_and_books", "Sugar cane drop enchanting tables and books");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "chickens_drop_arrows", "Chickens drop arrows");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "creepers_drop_tnt", "Creepers drop TNT");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "squids_drop_fishing_rods", "Squids drop fishing rods");
        moduleDescription(builder, UHCModules.FASTER_RESOURCES, "skeletons_drop_power_bow", "Skeletons drop Power-enchanted bows");
        moduleDescription(builder, UHCModules.POTION_DROPS, "zombies_drop_strength_potions", "Zombies drops potions of Strength");
        moduleDescription(builder, UHCModules.POTION_DROPS, "spiders_drop_poison_potions", "Spiders drops potions of Poison");
        moduleDescription(builder, UHCModules.POTION_DROPS, "sugar_cane_drops_swiftness_potions", "Sugar cane drops potions of Swiftness");
        moduleDescription(builder, UHCModules.POTION_DROPS, "rabbits_drop_leaping_potions", "Rabbits drops potions of Leaping");
        moduleDescription(builder, UHCModules.POTION_DROPS, "bats_drop_night_vision_potions", "Bats drops potions of Night Vision");

        // User interfaces
        builder.add("ui.uhc.modules.title", "Active Modules");
        builder.add("ui.uhc.create_uhc.title", "Create UHC");
        builder.add("ui.uhc.click_to_add", "Click to Add");
        builder.add("ui.uhc.click_to_remove", "Click to Remove");
        builder.add("ui.uhc.click_to_edit", "Click to Edit");
        builder.add("ui.uhc.click_to_select", "Click to Select");
        builder.add("ui.uhc.click_to_cycle", "%s / %s to change");
        builder.add("ui.uhc.incompatible_with", "Incompatible with %s");
        builder.add("ui.uhc.select_preset.title", "Pick a preset");
        builder.add("ui.uhc.confirm_launch.title", "Open this game?");
        builder.add("ui.uhc.summary", "Summary");
        builder.add("ui.uhc.confirm", "Open the game");
        builder.add("ui.uhc.confirm.description", "Opens the lobby for players to join.");
        builder.add("ui.uhc.preset", "Preset");
        builder.add("ui.uhc.preset.modules", "%s modules");
        builder.add("ui.uhc.team_size", "Team Size");
        builder.add("ui.uhc.team_size.description", "How many players share a team.");
        builder.add("ui.uhc.min_players", "Needs at least %s players");
        builder.add("ui.uhc.min_players.short", "Minimum players");
        builder.add("ui.uhc.option", "  %s");
        builder.add("ui.uhc.option.selected", "▶ %s");
        builder.add("text.uhc.modules.description", "Rule changes applied to the game.");
        builder.add("ui.uhc.more", "More Presets");
        builder.add("ui.uhc.more.description", "Everything else data packs provide.");
        builder.add("ui.uhc.discard.title", "Discard your changes?");
        builder.add("ui.uhc.discard", "Discard");
        builder.add("ui.uhc.discard.description", "Leaves without keeping anything you changed.");
        builder.add("ui.uhc.keep_editing", "Keep Editing");
        builder.add("ui.uhc.keep_editing.description", "Goes back to the creator.");

        // Settings
        builder.add("ui.uhc.setting.summary", "%s: %s");
        builder.add("ui.uhc.setting.value", "Currently: %s");
        builder.add("ui.uhc.setting.range", "Between %s and %s");
        builder.add("ui.uhc.setting.adjust", "%s / %s: ±%s");
        builder.add("ui.uhc.setting.adjust_big", "Hold %s: ±%s");
        builder.add("ui.uhc.unit.blocks", "%s blocks");
        builder.add("ui.uhc.unit.blocks_per_second", "%s blocks per second");

        builder.add("text.uhc.map.description", "The world and how its border closes in.");
        builder.add("text.uhc.timers.description", "How long each phase of the game lasts.");
        builder.add("game.custom_uhc.description", "Build your own UHC from any preset, then tweak everything.");

        builder.add("ui.uhc.setting.start_size_min", "Smallest Starting Border");
        builder.add("ui.uhc.setting.start_size_min.description", "Lower end of the random starting border.");
        builder.add("ui.uhc.setting.start_size_max", "Largest Starting Border");
        builder.add("ui.uhc.setting.start_size_max.description", "Upper end. Bigger means longer games.");
        builder.add("ui.uhc.setting.end_size_min", "Smallest Final Border");
        builder.add("ui.uhc.setting.end_size_min.description", "Lower end of the random final border.");
        builder.add("ui.uhc.setting.end_size_max", "Largest Final Border");
        builder.add("ui.uhc.setting.end_size_max.description", "Upper end. Smaller means a tighter fight.");
        builder.add("ui.uhc.setting.shrinking_speed", "Shrinking Speed");
        builder.add("ui.uhc.setting.shrinking_speed.description", "How fast the border closes in.");
        builder.add("ui.uhc.setting.spawn_offset", "Spawn Offset");
        builder.add("ui.uhc.setting.spawn_offset.description", "How far inside the border teams drop.");
        builder.add("ui.uhc.setting.cages", "Time in Cages");
        builder.add("ui.uhc.setting.cages.description", "Wait in the cages before each drop.");
        builder.add("ui.uhc.setting.invulnerability", "Invulnerability");
        builder.add("ui.uhc.setting.invulnerability.description", "No damage is taken after each drop.");
        builder.add("ui.uhc.setting.warmup", "Gathering Time");
        builder.add("ui.uhc.setting.warmup.description", "Time to mine and gear up before the finale.");
        builder.add("ui.uhc.setting.deathmatch", "Deathmatch Delay");
        builder.add("ui.uhc.setting.deathmatch.description", "Delay before the border starts hurting.");
        builder.add("ui.uhc.launch", "Launch");

        // [COMPAT] Ultimate Lucky Block
        builder.add("game.lucky_uhc", "Lucky UHC");
        builder.add("game.lucky_uhcrun", "Lucky UHCRun");
        builder.add("game.lucky_doublerunner", "Lucky DoubleRunner");
        module(builder, UHCModules.LUCKY_BLOCKS, "Lucky Blocks", "Lucky blocks spawn around the world");
    }

    /**
     * Adds the name of a module. Modules such as the {@code +} variants reuse another module's description, and
     * therefore have none of their own.
     */
    private static void module(TranslationBuilder builder, ResourceKey<?> key, String name) {
        builder.add(translationKey(key), name);
    }

    private static void module(TranslationBuilder builder, ResourceKey<?> key, String name, String description) {
        module(builder, key, name);
        builder.add(translationKey(key) + ".description", description);
    }

    private static void moduleDescription(TranslationBuilder builder, ResourceKey<?> key, String path, String description) {
        builder.add(translationKey(key) + ".description." + path, description);
    }

    private static String translationKey(ResourceKey<?> key) {
        return Util.makeDescriptionId("module", key.identifier());
    }
}
