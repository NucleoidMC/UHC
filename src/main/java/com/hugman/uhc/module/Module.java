package com.hugman.uhc.module;

import com.hugman.uhc.module.piece.Modifier;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record Module(String translation, Optional<Either<String, List<String>>> description, Item icon, TextColor color,
					 List<Modifier> pieces) {
	public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("translation").forGetter(Module::translation),
			Codec.either(Codec.STRING, Codec.STRING.listOf()).optionalFieldOf("description").forGetter(Module::description),
			Registries.ITEM.getCodec().optionalFieldOf("icon", Items.BARRIER).forGetter(Module::icon),
			TextColor.CODEC.optionalFieldOf("color", TextColor.parse("#39db7f")).forGetter(Module::color),
			Modifier.TYPE_CODEC.listOf().fieldOf("modifiers").forGetter(Module::pieces)
	).apply(instance, Module::new));

	public List<String> getDescriptionLines() {
		List<String> list = new ArrayList<>();
		if (description.isPresent()) {
			Either<String, List<String>> either = description.get();
			either.ifLeft(list::add);
			either.ifRight(list::addAll);
		} else {
			list.add(translation() + ".description");
		}
		return list;
	}
}
