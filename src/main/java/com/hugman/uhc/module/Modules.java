package com.hugman.uhc.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
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
import java.util.Set;

public final class Modules {
	private static final TinyRegistry<Module> REGISTRY = TinyRegistry.newStable();

	private Modules() {
	}

	public static void register() {
		ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);

		serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(Plasmid.ID, "modules");
			}

			@Override
			public void reload(ResourceManager manager) {
				REGISTRY.clear();

				Collection<Identifier> resources = manager.findResources("modules", path -> path.endsWith(".json"));

				for(Identifier path : resources) {
					try {
						Resource resource = manager.getResource(path);
						try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
							JsonElement json = new JsonParser().parse(reader);

							Identifier identifier = identifierFromPath(path);

							DataResult<Module> result = Module.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

							result.result().ifPresent(module -> REGISTRY.register(identifier, module));
							result.error().ifPresent(error -> Plasmid.LOGGER.error("Failed to decode module at {}: {}", path, error.toString()));
						}
					}
					catch(IOException e) {
						Plasmid.LOGGER.error("Failed to read module at {}", path, e);
					}
				}
			}
		});
	}

	private static Identifier identifierFromPath(Identifier location) {
		String path = location.getPath();
		path = path.substring("modules/".length(), path.length() - ".json".length());
		return new Identifier(location.getNamespace(), path);
	}

	@Nullable
	public static Module get(Identifier identifier) {
		return REGISTRY.get(identifier);
	}

	public static Set<Identifier> getKeys() {
		return REGISTRY.keySet();
	}
}
