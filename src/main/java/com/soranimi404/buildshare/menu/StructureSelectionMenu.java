package com.soranimi404.buildshare.menu;

import com.soranimi404.buildshare.init.ModMenus;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.List;

public class StructureSelectionMenu extends AbstractContainerMenu {

    private final Player player;
    private final List<Path> structureFiles;
    private final Container fileContainer = new SimpleContainer(9);
    private final BlockPos blockPos;

    public StructureSelectionMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, BlockPos.ZERO);
    }

    public StructureSelectionMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, data.readBlockPos());
    }

    public StructureSelectionMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        super(ModMenus.STRUCTURE_SELECTION_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.blockPos = blockPos;

        // 加载所有结构文件
        this.structureFiles = StructureLoader.listStructureFiles();

        // 创建文件表示物品
        for (int i = 0; i < Math.min(9, structureFiles.size()); i++) {
            Path file = structureFiles.get(i);
            String fileName = file.getFileName().toString().replace(".nbt", "");
            ItemStack stack = new ItemStack(Items.PAPER);
            stack.setHoverName(Component.literal(fileName));
            fileContainer.setItem(i, stack);
        }

        // 文件槽位 (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                this.addSlot(new Slot(fileContainer, index, 62 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false; // 禁止放入物品
                    }
                });
            }
        }

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
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // 禁止物品移动
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, net.minecraft.world.inventory.ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 9 && slotId < structureFiles.size()) {
            Path selectedFile = structureFiles.get(slotId);
            String fileName = selectedFile.getFileName().toString();

            // 打开材料提交界面
            if (player instanceof ServerPlayer serverPlayer) {
                MaterialSubmitMenu.open(serverPlayer, blockPos, fileName);
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }
}