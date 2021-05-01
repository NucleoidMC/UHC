package com.hugman.uhc.module;

import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class Module {
	public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("translation").forGetter(module -> module.translation),
			ItemStack.CODEC.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(module -> module.icon),
			ModulePieces.CODEC.listOf().fieldOf("pieces").forGetter(module -> module.pieces)
	).apply(instance, Module::new));

	private final String translation;
	private final ItemStack icon;
	private final List<ModulePiece> pieces;

	public Module(String translation, ItemStack icon, List<ModulePiece> pieces) {
		this.translation = translation;
		this.icon = icon;
		this.pieces = pieces;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public List<ModulePiece> getPieces() {
		return pieces;
	}

	public TranslatableText getName() {
		return new TranslatableText(translation);
	}
}
