package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.block.ExportMarkerBlock;
import com.soranimi404.buildshare.block.ImportBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "buildshare");

    public static final RegistryObject<Block> EXPORT_MARKER = BLOCKS.register(
            "export_marker",
            () -> new ExportMarkerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD))
    );
    public static final RegistryObject<Block> IMPORT_BLOCK = BLOCKS.register(
            "import_block",
            () -> new ExportMarkerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD))
    );
}