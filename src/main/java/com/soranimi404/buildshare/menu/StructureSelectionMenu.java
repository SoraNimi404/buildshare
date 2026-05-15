package com.soranimi404.buildshare.menu;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.init.ModMenus;
import com.soranimi404.buildshare.util.StructureBuilder;
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
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.List;

public class StructureSelectionMenu extends AbstractContainerMenu {

    private final Player player;
    private final List<Path> structureFiles;
    private final Container fileContainer = new SimpleContainer(27);
    private final BlockPos blockPos;
    private int currentPage;

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
        this.currentPage = 0;

        this.structureFiles = StructureLoader.listStructureFiles();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                this.addSlot(new Slot(fileContainer, index, 8 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        updatePageItems();
    }

    private void updatePageItems() {
        int start = currentPage * 27;
        for (int i = 0; i < 27; i++) {
            int fileIdx = start + i;
            if (fileIdx < structureFiles.size()) {
                Path file = structureFiles.get(fileIdx);
                String fileName = file.getFileName().toString().replace(".nbt", "");
                ItemStack stack = new ItemStack(Items.PAPER);
                stack.setHoverName(Component.literal(fileName));
                fileContainer.setItem(i, stack);
            } else {
                fileContainer.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return Math.max(1, (structureFiles.size() + 26) / 27);
    }

    public void goToPage(int page) {
        if (page >= 0 && page < getTotalPages()) {
            currentPage = page;
            updatePageItems();
        }
    }

    public List<Path> getPageFiles() {
        int start = currentPage * 27;
        int end = Math.min(start + 27, structureFiles.size());
        return structureFiles.subList(start, end);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        int fileIdx = currentPage * 27 + slotId;
        if (slotId >= 0 && slotId < 27 && fileIdx < structureFiles.size()) {
            Path selectedFile = structureFiles.get(fileIdx);
            if (player instanceof ServerPlayer) {
                if (dragType == 1) {
                    String fileName = selectedFile.getFileName().toString().replace(".nbt", "");
                    com.soranimi404.buildshare.network.RequestPreviewPacket packet =
                            new com.soranimi404.buildshare.network.RequestPreviewPacket(fileName, blockPos);
                    com.soranimi404.buildshare.network.PacketHandler.INSTANCE.sendToServer(packet);
                    player.closeContainer();
                } else {
                    BuildShareData.StructureCapture capture = StructureLoader.loadStructure(selectedFile);
                    if (capture != null) {
                        StructureBuilder.buildStructure(
                                player.level(),
                                blockPos,
                                capture
                        );
                        player.displayClientMessage(Component.literal("§a建筑生成成功！"), true);
                    } else {
                        player.displayClientMessage(Component.literal("§c加载建筑失败！"), true);
                    }
                    player.closeContainer();
                }
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }
}