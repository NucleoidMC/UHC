package com.hugman.uhc.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hugman.uhc.UHC;
import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieces;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.Plasmid;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record UHCModule(String translation, Optional<String> description, ItemStack icon, List<ModulePiece> pieces) {
	public static final Codec<UHCModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("translation").forGetter(UHCModule::translation),
			Codec.STRING.optionalFieldOf("description").forGetter(UHCModule::description),
			ItemStack.CODEC.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(UHCModule::icon),
			ModulePieces.CODEC.listOf().fieldOf("pieces").forGetter(UHCModule::pieces)
	).apply(instance, UHCModule::new));

	private static final TinyRegistry<UHCModule> REGISTRY = TinyRegistry.create();

	public static void register() {
		ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);

		serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return UHC.id("modules");
			}

			@Override
			public void reload(ResourceManager manager) {
				REGISTRY.clear();

				Collection<Identifier> resources = manager.findResources("uhc_modules", path -> path.endsWith(".json"));

				for(Identifier path : resources) {
					try {
						Resource resource = manager.getResource(path);
						try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
							JsonElement json = new JsonParser().parse(reader);

							Identifier identifier = identifierFromPath(path);

							DataResult<UHCModule> result = UHCModule.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

							result.result().ifPresent(module -> REGISTRY.register(identifier, module));
							result.error().ifPresent(error -> Plasmid.LOGGER.error("Failed to decode UHC module at {}: {}", path, error.toString()));
						}
					}
					catch(IOException e) {
						Plasmid.LOGGER.error("Failed to read UHC module at {}", path, e);
					}
				}
			}
		});
	}

	private static Identifier identifierFromPath(Identifier location) {
		String path = location.getPath();
		path = path.substring("uhc_modules/".length(), path.length() - ".json".length());
		return new Identifier(location.getNamespace(), path);
	}

	@Nullable
	public static UHCModule get(Identifier identifier) {
		return REGISTRY.get(identifier);
	}

	public static Set<Identifier> getKeys() {
		return REGISTRY.keySet();
	}
}
