package com.hugman.uhc.module;

import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Optional;

public class Module {
	public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("translation").forGetter(module -> module.translation),
			Codec.STRING.optionalFieldOf("description").forGetter(module -> Optional.of(module.description)),
			ItemStack.CODEC.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(module -> module.icon),
			ModulePieces.CODEC.listOf().fieldOf("pieces").forGetter(module -> module.pieces)
	).apply(instance, Module::new));

	private final String translation;
	private final String description;
	private final ItemStack icon;
	private final List<ModulePiece> pieces;

	public Module(String translation, Optional<String> description, ItemStack icon, List<ModulePiece> pieces) {
		this.translation = translation;
		this.description = description.orElseGet(() -> translation + "description");
		this.icon = icon;
		this.pieces = pieces;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public List<ModulePiece> getPieces() {
		return pieces;
	}

	public String getTranslation() {
		return translation;
	}
}
