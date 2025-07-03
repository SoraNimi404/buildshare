package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureSelectionScreen extends AbstractContainerScreen<StructureSelectionMenu> {
    private static final Logger LOGGER = LogManager.getLogger();

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
        // 添加渲染日志
        LOGGER.debug("渲染 StructureSelectionScreen 背景");

        // 渲染背景
        renderBackground(guiGraphics);

        // 计算屏幕中心位置
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 添加调试信息
        if (this.minecraft != null && this.minecraft.player != null) {
            String debugInfo = String.format("屏幕尺寸: %dx%d, 图像尺寸: %dx%d, 位置: (%d,%d)",
                    width, height, imageWidth, imageHeight, x, y);
            LOGGER.debug(debugInfo);
        }

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

        // 添加鼠标位置调试
        if (this.minecraft != null && this.minecraft.player != null) {
            String mousePos = String.format("鼠标: (%d, %d)", mouseX, mouseY);
            guiGraphics.drawString(
                    font,
                    mousePos,
                    mouseX + 10,
                    mouseY - 10,
                    0xFFFFFF, // 白色
                    false
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        LOGGER.info("鼠标点击: X=" + mouseX + ", Y=" + mouseY + ", 按钮=" + button);

        // 计算屏幕中心位置
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 检查是否点击文件区域 (示例)
        if (mouseX >= x + 10 && mouseX <= x + 150 &&
                mouseY >= y + 40 && mouseY <= y + 140) {
            LOGGER.info("点击文件区域");
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(
                       Component.literal("§a选择建筑文件 (待实现)"),
                        false
                );
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}