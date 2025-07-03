package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
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

    // 材料提交菜单
    public static final RegistryObject<MenuType<MaterialSubmitMenu>> MATERIAL_SUBMIT_MENU = MENUS.register(
            "material_submit_menu",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new MaterialSubmitMenu(windowId, inv, data.readBlockPos(), data.readUtf()))
    );
}