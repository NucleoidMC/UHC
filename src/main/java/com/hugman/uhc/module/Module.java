package com.hugman.uhc.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hugman.uhc.UHC;
import com.hugman.uhc.UHCRegistries;
import com.hugman.uhc.module.piece.ModulePiece;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.codecs.MoreCodecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record Module(String translation, Optional<Either<String, List<String>>> description, Item icon, TextColor color, List<ModulePiece> pieces) {
	public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("translation").forGetter(Module::translation),
			Codec.either(Codec.STRING, Codec.STRING.listOf()).optionalFieldOf("description").forGetter(Module::description),
			Registry.ITEM.getCodec().optionalFieldOf("icon", Items.BARRIER).forGetter(Module::icon),
			MoreCodecs.TEXT_COLOR.optionalFieldOf("color", TextColor.parse("#39db7f")).forGetter(Module::color),
			ModulePiece.TYPE_CODEC.listOf().fieldOf("pieces").forGetter(Module::pieces)
	).apply(instance, Module::new));

	public static void register() {
		ResourceManagerHelper serverData = ResourceManagerHelper.get(ResourceType.SERVER_DATA);

		serverData.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return UHC.id("modules");
			}

			@Override
			public void reload(ResourceManager manager) {
				UHCRegistries.MODULES.clear();

				Collection<Identifier> resources = manager.findResources("uhc_modules", path -> path.endsWith(".json"));

				for(Identifier path : resources) {
					try {
						Resource resource = manager.getResource(path);
						try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
							JsonElement json = JsonParser.parseReader(reader);

							Identifier identifier = identifierFromPath(path);

							DataResult<Module> result = Module.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

							result.result().ifPresent(module -> UHCRegistries.MODULES.register(identifier, module));
							result.error().ifPresent(error -> UHC.LOGGER.error("Failed to decode UHC module at {}: {}", path, error.toString()));
						}
					} catch(IOException e) {
						UHC.LOGGER.error("Failed to read UHC module at {}", path, e);
					}
				}
			}
		});
	}

	private static Identifier identifierFromPath(Identifier id) {
		String path = id.getPath();
		path = path.substring("uhc_modules/".length(), path.length() - ".json".length());
		return new Identifier(id.getNamespace(), path);
	}

	public List<String> getDescriptionLines() {
		List<String> list = new ArrayList<>();
		if(description.isPresent()) {
			Either<String, List<String>> either = description.get();
			either.ifLeft(list::add);
			either.ifRight(list::addAll);
		}
		else {
			list.add(translation() + ".description");
		}
		return list;
	}
}
