package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;

public class StructureSelectionScreen extends AbstractContainerScreen<StructureSelectionMenu> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation CONTAINER_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");

    public StructureSelectionScreen(StructureSelectionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 168;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // 渲染背景
        renderBackground(guiGraphics);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 绘制UI背景
        guiGraphics.blit(CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);



        // 绘制结构文件列表
        drawStructureFiles(guiGraphics, x, y);
    }

    private void drawStructureFiles(GuiGraphics guiGraphics, int x, int y) {
        List<Path> structureFiles = menu.getStructureFiles();

        for (int i = 0; i < Math.min(9, structureFiles.size()); i++) {
            Path file = structureFiles.get(i);
            String fileName = getFileNameWithoutExtension(file);

            // 创建表示文件的物品
            ItemStack fileItem = new ItemStack(Items.PAPER);
            fileItem.setHoverName(Component.literal(fileName));

            // 计算槽位位置
            int slotX = x + 62 + (i % 3) * 18;
            int slotY = y + 17 + (i / 3) * 18;

            // 绘制物品
            guiGraphics.renderItem(fileItem, slotX, slotY);
            guiGraphics.renderItemDecorations(font, fileItem, slotX, slotY);
        }
    }

    private String getFileNameWithoutExtension(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 检查是否点击了文件区域
        for (int i = 0; i < Math.min(9, menu.getStructureFiles().size()); i++) {
            int slotX = x + 62 + (i % 3) * 18;
            int slotY = y + 17 + (i / 3) * 18;

            if (mouseX >= slotX && mouseX <= slotX + 16 &&
                    mouseY >= slotY && mouseY <= slotY + 16) {
                menu.clicked(i, 0, net.minecraft.world.inventory.ClickType.PICKUP, minecraft.player);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}