package com.soranimi404.buildshare.init; // 更新包名

import com.soranimi404.buildshare.BuildShareMod; // 更新引用
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "buildshare");

    public static final RegistryObject<CreativeModeTab> BUILDSHARE_TAB = CREATIVE_MODE_TABS.register(
            "buildshare_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.EXPORT_STICK.get()))
                    .title(Component.translatable("itemGroup.buildshare"))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.EXPORT_STICK.get());
                        output.accept(ModItems.EXPORT_MARKER_ITEM.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}