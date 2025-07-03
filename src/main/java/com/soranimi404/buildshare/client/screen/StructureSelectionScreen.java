package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StructureSelectionScreen extends AbstractContainerScreen<StructureSelectionMenu> {
    // 使用原版箱子UI纹理
    private static final ResourceLocation CONTAINER_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");

    public StructureSelectionScreen(StructureSelectionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 168;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 使用 GuiGraphics 绘制背景
        guiGraphics.blit(CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 添加标题 - 使用 GuiGraphics 绘制文本
        guiGraphics.drawString(
                font,
                "选择建筑",
                x + 8,
                y + 6,
                0x404040,
                false // 不绘制阴影
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}