package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.menu.ImportMenu;
import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "buildshare");

    // 结构选择菜单
    public static final RegistryObject<MenuType<ImportMenu>> IMPORT_MENU = MENUS.register(
            "import_menu",
            () -> IForgeMenuType.create(ImportMenu::new)
    );

    // 材料提交菜单
    public static final RegistryObject<MenuType<MaterialSubmitMenu>> MATERIAL_MENU = MENUS.register(
            "material_menu",
            () -> IForgeMenuType.create(MaterialSubmitMenu::new)
    );
}