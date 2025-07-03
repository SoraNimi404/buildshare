package com.soranimi404.buildshare.menu;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.nio.file.Path;
import java.util.List;

public class ImportMenu extends AbstractContainerMenu {

    private final Player player;
    private final List<Path> structureFiles;

    public ImportMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, playerInventory.player);
    }

    public ImportMenu(int containerId, Inventory playerInventory, Player player) {
        super(ModMenus.IMPORT_MENU.get(), containerId);
        this.player = player;

        // 加载所有结构文件
        this.structureFiles = StructureLoader.listStructureFiles();

        // 玩家物品栏
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 玩家快捷栏
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        // 结构文件槽位
        for (int i = 0; i < Math.min(9, structureFiles.size()); i++) {
            Path file = structureFiles.get(i);
            ItemStack stack = new ItemStack(Items.PAPER);
            stack.setHoverName(Component.literal(file.getFileName().toString()));
            this.addSlot(new SlotItemHandler(new ItemStackHandler(new ItemStack[]{stack}), i, 8 + i * 18, 20));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // 禁用快速移动
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // 当玩家点击结构文件时
    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 36 && slotId < 36 + structureFiles.size()) {
            int fileIndex = slotId - 36;
            Path selectedFile = structureFiles.get(fileIndex);

            // 打开材料提交界面
            if (player instanceof ServerPlayer serverPlayer) {
                MaterialSubmitMenu.open(serverPlayer, selectedFile);
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }
}