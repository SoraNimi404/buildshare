package com.soranimi404.buildshare.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Map;

public class MaterialSubmitScreen extends AbstractContainerScreen<MaterialSubmitMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("buildshare", "textures/gui/material_submit.png");

    public MaterialSubmitScreen(MaterialSubmitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 渲染材料需求
        int textY = y + 5;
        for (Map.Entry<String, Integer> entry : menu.requiredMaterials.entrySet()) {
            int submitted = menu.submittedMaterials.getOrDefault(entry.getKey(), 0);
            String status = submitted >= entry.getValue() ? "§a✓" : "§c✗";

            String text = String.format("%s %s: %d/%d",
                    status, getItemName(entry.getKey()), submitted, entry.getValue());

            font.draw(poseStack, text, x + 60, textY, 0xFFFFFF);
            textY += 10;
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        // 生成按钮
        if (menu.canGenerate()) {
            int buttonX = (this.imageWidth - 50) / 2;
            int buttonY = this.imageHeight - 30;

            fill(poseStack, buttonX, buttonY, buttonX + 50, buttonY + 20, 0xFF00FF00);
            font.draw(poseStack, "生成", buttonX + 15, buttonY + 6, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击生成按钮
        if (menu.canGenerate()) {
            int buttonX = (this.width - this.imageWidth) / 2 + (this.imageWidth - 50) / 2;
            int buttonY = (this.height - this.imageHeight) / 2 + this.imageHeight - 30;

            if (mouseX >= buttonX && mouseX <= buttonX + 50 &&
                    mouseY >= buttonY && mouseY <= buttonY + 20) {

                // 发送生成请求到服务器
                // 需要实现网络包处理
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String getItemName(String itemId) {
        ResourceLocation id = new ResourceLocation(itemId);
        return ForgeRegistries.ITEMS.getValue(id).getDescription().getString();
    }
}