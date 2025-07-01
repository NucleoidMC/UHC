package fr.hugman.uhc.impl.command.argument;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.impl.game.ModuleManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public final class UHCModuleArgument {
    private static final DynamicCommandExceptionType MODULE_NOT_FOUND = new DynamicCommandExceptionType((id) -> Text.stringifiedTranslatable("text.module.not_found", id)); //TODO: change

    public static RequiredArgumentBuilder<ServerCommandSource, Identifier> argumentFromEnabled(String name) {
        return CommandManager.argument(name, IdentifierArgumentType.identifier()).suggests((ctx, builder) -> {
            Registry<UHCModule> registry = ctx.getSource().getRegistryManager().getOrThrow(UHCRegistryKeys.MODULE);
            String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
            var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(ctx.getSource().getWorld())).getAttachment(ModuleManager.ATTACHMENT);
            if (manager == null) {
                return builder.buildFuture();
            }
            var enabledKeys = manager.keys();
            CommandSource.forEachMatching(enabledKeys, remaining, RegistryKey::getValue, (key) -> registry.getOptional(key)
                    .ifPresent((entry) -> builder.suggest(key.getValue().toString(), entry.value().name())));
            return builder.buildFuture();
        });
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Identifier> argumentFromDisabled(String name) {
        return CommandManager.argument(name, IdentifierArgumentType.identifier()).suggests((ctx, builder) -> {
            Registry<UHCModule> registry = ctx.getSource().getRegistryManager().getOrThrow(UHCRegistryKeys.MODULE);
            String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
            var manager = Objects.requireNonNull(GameSpaceManager.get().byWorld(ctx.getSource().getWorld())).getAttachment(ModuleManager.ATTACHMENT);
            var candidates = new ArrayList<>(registry.getKeys());
            if (manager != null) {
                candidates.removeAll(manager.keys());
            }
            CommandSource.forEachMatching(candidates, remaining, RegistryKey::getValue, (key) -> registry.getOptional(key)
                    .ifPresent((entry) -> builder.suggest(key.getValue().toString(), entry.value().name())));
            return builder.buildFuture();
        });
    }

    public static RegistryEntry.Reference<UHCModule> get(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryKey<UHCModule> key = RegistryKey.of(UHCRegistryKeys.MODULE, IdentifierArgumentType.getIdentifier(context, name));
        Registry<UHCModule> registry = context.getSource().getRegistryManager().getOrThrow(UHCRegistryKeys.MODULE);
        return registry.getOptional(key).orElseThrow(() -> MODULE_NOT_FOUND.create(key.getValue()));
    }
}
