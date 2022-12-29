package com.hugman.uhc.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hugman.uhc.UHC;
import com.hugman.uhc.UHCRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ModuleManager {
	private static final TinyRegistry<Module> REGISTRY = UHCRegistries.MODULE;
	private static final Identifier ID = UHC.id("module");
	private static final String PATH = "uhc/module";

	public static void register() {
		ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);

		serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {

			@Override
			public Identifier getFabricId() {
				return ID;
			}

			@Override
			public void reload(ResourceManager manager) {
				REGISTRY.clear();

				var resources = manager.findResources(PATH, path -> path.getPath().endsWith(".json"));

				for (var path : resources.entrySet()) {
					try {
						try (Reader reader = new BufferedReader(new InputStreamReader(path.getValue().getInputStream()))) {
							JsonElement json = new JsonParser().parse(reader);

							Identifier identifier = identifierFromPath(path.getKey());

							DataResult<Module> result = Module.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

							result.result().ifPresent(game -> REGISTRY.register(identifier, game));

							result.error().ifPresent(error -> UHC.LOGGER.error("Failed to decode UHC module at {}: {}", path, error.toString()));
						}
					} catch (IOException e) {
						UHC.LOGGER.error("Failed to decode UHC module at {}", path, e);
					}
				}
			}
		});
	}

	private static Identifier identifierFromPath(Identifier location) {
		String path = location.getPath();
		path = path.substring(PATH.length() + 1, path.length() - ".json".length());
		return new Identifier(location.getNamespace(), path);
	}
}