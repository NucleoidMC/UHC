package fr.hugman.uhc.api.command;

import com.mojang.brigadier.arguments.ArgumentType;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.api.command.argument.UHCConfigArgument;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class UHCArgumentTypes {
    public static final ArgumentSerializer<UHCConfigArgument, ConstantArgumentSerializer<UHCConfigArgument>.Properties> UHC_CONFIG = of("uhc_config", UHCConfigArgument.class, ConstantArgumentSerializer.of(UHCConfigArgument::new));

    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> of(String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(UHC.id(id), clazz, serializer);
        return serializer;
    }
}
