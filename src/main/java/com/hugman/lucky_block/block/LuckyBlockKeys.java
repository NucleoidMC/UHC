package com.hugman.lucky_block.block;

import com.hugman.lucky_block.LuckyBlockMod;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LuckyBlockKeys {
    public static final RegistryKey<Block> LUCKY_BLOCK = of("lucky_block");

    private static RegistryKey<Block> of(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, LuckyBlockMod.id(path));
    }
}
