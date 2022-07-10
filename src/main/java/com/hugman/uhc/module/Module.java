package com.hugman.uhc.module;

import com.hugman.uhc.UHCRegistries;
import com.hugman.uhc.module.piece.ModulePiece;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import xyz.nucleoid.codecs.MoreCodecs;

import java.util.ArrayList;
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

	public static final Codec<RegistryEntry<Module>> REGISTRY_CODEC = RegistryElementCodec.of(UHCRegistries.MODULE.getKey(), CODEC);
	public static final Codec<RegistryEntryList<Module>> REGISTRY_LIST_CODEC = RegistryCodecs.entryList(UHCRegistries.MODULE.getKey(), CODEC);

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
