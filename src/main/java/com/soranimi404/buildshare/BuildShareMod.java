package com.soranimi404.buildshare;

import com.soranimi404.buildshare.init.ModBlocks;
import com.soranimi404.buildshare.init.ModItems;
import com.soranimi404.buildshare.init.ModTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("buildshare")
public class BuildShareMod {

    public BuildShareMod() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.register(modEventBus);

    }
}