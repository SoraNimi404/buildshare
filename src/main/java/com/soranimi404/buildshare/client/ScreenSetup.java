package com.soranimi404.buildshare.client;

import com.soranimi404.buildshare.client.screen.StructureSelectionScreen;
import com.soranimi404.buildshare.init.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ScreenSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册结构选择菜单
            MenuScreens.register(ModMenus.STRUCTURE_SELECTION_MENU.get(), StructureSelectionScreen::new);
        });
    }
}