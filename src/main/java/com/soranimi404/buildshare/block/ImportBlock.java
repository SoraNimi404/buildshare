package com.soranimi404.buildshare.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ImportBlock extends Block {
    public ImportBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .strength(2.5f)
                .requiresCorrectToolForDrops());
    }
}