package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class MaterialSubmitScreen extends AbstractContainerScreen<MaterialSubmitMenu> {

    // 使用原版工作台UI纹理
    private static final ResourceLocation CRAFTING_TABLE_TEXTURE =
            new ResourceLocation("textures/gui/container/crafting_table.png");

    public MaterialSubmitScreen(MaterialSubmitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 使用 GuiGraphics 绘制背景
        guiGraphics.blit(CRAFTING_TABLE_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 绘制材料需求
        int textY = y + 10;
        for (Map.Entry<String, Integer> entry : menu.requiredMaterials.entrySet()) {
            int submitted = menu.submittedMaterials.getOrDefault(entry.getKey(), 0);
            String status = submitted >= entry.getValue() ? "✓" : "✗";
            String colorCode = submitted >= entry.getValue() ? "§a" : "§c";

            String itemName = getItemName(entry.getKey());
            String text = String.format("%s%s %s: %d/%d",
                    colorCode, status, itemName, submitted, entry.getValue());

            // 使用 GuiGraphics 绘制文本
            guiGraphics.drawString(
                    font,
                    text,
                    x + 10,
                    textY,
                    0xFFFFFF,
                    false // 不绘制阴影
            );
            textY += 12;
        }

        // 生成按钮 - 使用 GuiGraphics 绘制矩形和文本
        if (menu.canGenerate()) {
            // 绘制绿色按钮
            guiGraphics.fill(x + 100, y + 150, x + 150, y + 170, 0xFF00FF00);

            // 绘制按钮文本
            guiGraphics.drawString(
                    font,
                    "生成建筑",
                    x + 108,
                    y + 156,
                    0x000000,
                    false // 不绘制阴影
            );
        }
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


        if (menu.canGenerate() &&
                mouseX >= x + 100 && mouseX <= x + 150 &&
                mouseY >= y + 150 && mouseY <= y + 170) {

            menu.generateStructure();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private String getItemName(String itemId) {

        ResourceLocation resource = ResourceLocation.tryParse(itemId);
        if (resource != null) {
            Item item = ForgeRegistries.ITEMS.getValue(resource);
            return item != null ? item.getDescription().getString() : itemId;
        }
        return itemId; // 如果解析失败，返回原始ID
    }
}