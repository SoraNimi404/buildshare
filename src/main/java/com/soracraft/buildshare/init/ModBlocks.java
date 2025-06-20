package com.soracraft.buildshare.init;

import com.soracraft.buildshare.block.ExportMarkerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "buildshare");

    public static final RegistryObject<Block> EXPORT_MARKER = BLOCKS.register("export_marker",
            () -> new ExportMarkerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0f)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
