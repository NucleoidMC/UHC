package fr.hugman.uhc.api.command.argument;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Locale;

public final class UHCConfigArgument {
    private static final DynamicCommandExceptionType CONFIG_NOT_FOUND = new DynamicCommandExceptionType((id) -> Text.stringifiedTranslatable("text.uhc.config.not_found", id));

    public static RequiredArgumentBuilder<ServerCommandSource, Identifier> argument(String name) {
        return CommandManager.argument(name, IdentifierArgumentType.identifier()).suggests((ctx, builder) -> {
            Registry<UHCConfig> registry = ctx.getSource().getRegistryManager().getOrThrow(UHCRegistryKeys.UHC_CONFIG);
            CommandSource.forEachMatching(registry.getKeys(),
                    builder.getRemaining().toLowerCase(Locale.ROOT),
                    RegistryKey::getValue,
                    (key) -> registry.getOptional(key)
                            .ifPresent((entry) -> builder.suggest(key.getValue().toString())));
            return builder.buildFuture();
        });
    }

    public static RegistryEntry.Reference<UHCConfig> get(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryKey<UHCConfig> key = RegistryKey.of(UHCRegistryKeys.UHC_CONFIG, IdentifierArgumentType.getIdentifier(context, name));
        Registry<UHCConfig> registry = context.getSource().getRegistryManager().getOrThrow(UHCRegistryKeys.UHC_CONFIG);
        return registry.getOptional(key).orElseThrow(() -> CONFIG_NOT_FOUND.create(key.getValue()));
    }
}