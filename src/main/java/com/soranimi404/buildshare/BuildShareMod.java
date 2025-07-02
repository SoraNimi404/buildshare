package com.soranimi404.buildshare;

import com.soranimi404.buildshare.init.ModBlocks;
import com.soranimi404.buildshare.init.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("buildshare")
public class BuildShareMod {

    public BuildShareMod() {
        // 传统方式获取 modEventBus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
       // LOGGER.info("BuildShare Mod 正在初始化...");
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

    }
}