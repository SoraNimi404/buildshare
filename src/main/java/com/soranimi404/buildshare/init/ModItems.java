package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.item.ExportStickItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "buildshare");

    public static final RegistryObject<Item> EXPORT_MARKER_ITEM = ITEMS.register(
            "export_marker",
            () -> new BlockItem(ModBlocks.EXPORT_MARKER.get(), new Item.Properties())
    );
   public static final RegistryObject<Item> EXPORT_STICK = ITEMS.register(
            "export_stick",
            () -> new ExportStickItem(new Item.Properties())
    );
    public static final RegistryObject<Item> IMPORT_BLOCK_ITEM = ITEMS.register(
            "import_block",
            () -> new BlockItem(ModBlocks.IMPORT_BLOCK.get(), new Item.Properties())
    );
}