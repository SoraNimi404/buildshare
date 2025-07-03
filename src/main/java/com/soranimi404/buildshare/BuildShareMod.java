package com.soranimi404.buildshare;

import com.soranimi404.buildshare.event.ModEvents;
import com.soranimi404.buildshare.init.*;
import com.soranimi404.buildshare.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("buildshare")
public class BuildShareMod {

    public BuildShareMod() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ModEvents.class);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        PacketHandler.register();
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }






}