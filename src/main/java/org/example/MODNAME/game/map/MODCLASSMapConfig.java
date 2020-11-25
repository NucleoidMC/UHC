package org.example.MODNAME.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class MODCLASSMapConfig {
    public static final Codec<MODCLASSMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock)
    ).apply(instance, MODCLASSMapConfig::new));

    public final BlockState spawnBlock;

    public MODCLASSMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
