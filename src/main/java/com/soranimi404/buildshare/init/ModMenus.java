package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "buildshare");

    // 结构选择菜单
    public static final RegistryObject<MenuType<StructureSelectionMenu>> STRUCTURE_SELECTION_MENU = MENUS.register(
            "structure_selection_menu",
            () -> IForgeMenuType.create(StructureSelectionMenu::new)
    );


}