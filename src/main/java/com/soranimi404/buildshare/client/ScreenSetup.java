package com.soranimi404.buildshare.client;

import com.soranimi404.buildshare.init.ModMenus;
import com.soranimi404.buildshare.client.screen.MaterialSubmitScreen; // 添加导入
import com.soranimi404.buildshare.client.screen.StructureSelectionScreen; // 添加导入
import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
import com.soranimi404.buildshare.menu.StructureSelectionMenu;
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
            // 使用原版容器屏幕
            MenuScreens.register(ModMenus.STRUCTURE_SELECTION_MENU.get(), StructureSelectionScreen::new);
            MenuScreens.register(ModMenus.MATERIAL_SUBMIT_MENU.get(), MaterialSubmitScreen::new);
        });
    }
}