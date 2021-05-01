package com.hugman.uhc.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ConfiguredModule {
	public static final Codec<ConfiguredModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ItemStack.CODEC.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(ConfiguredModule::getIcon),
			Modules.CODEC.fieldOf("module").forGetter(ConfiguredModule::getModule)
	).apply(instance, ConfiguredModule::new));

	private final ItemStack icon;
	private final Module module;

	public ConfiguredModule(ItemStack icon, Module module) {
		this.icon = icon;
		this.module = module;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public Module getModule() {
		return module;
	}
}
