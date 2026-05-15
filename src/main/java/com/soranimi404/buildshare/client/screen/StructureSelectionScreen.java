package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import com.soranimi404.buildshare.util.StructureFileHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureSelectionScreen extends AbstractContainerScreen<StructureSelectionMenu> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation CONTAINER_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");
    private final Map<Integer, Map<String, Integer>> materialsCache = new HashMap<>();

    public StructureSelectionScreen(StructureSelectionMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 95;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(CONTAINER_TEXTURE, x, y, 0, 0, imageWidth, 71);
        guiGraphics.fill(x, y + 71, x + imageWidth, y + imageHeight, 0xFFC6C6C6);

        drawStructureFiles(guiGraphics, x, y);
        drawPageButtons(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0x404040);
    }

    private void drawStructureFiles(GuiGraphics guiGraphics, int x, int y) {
        List<Path> pageFiles = menu.getPageFiles();

        for (int i = 0; i < pageFiles.size(); i++) {
            Path file = pageFiles.get(i);
            String fileName = getFileNameWithoutExtension(file);

            ItemStack fileItem = new ItemStack(Items.PAPER);
            fileItem.setHoverName(Component.literal(fileName));

            int slotX = x + 8 + (i % 9) * 18;
            int slotY = y + 17 + (i / 9) * 18;

            guiGraphics.renderItem(fileItem, slotX, slotY);
            guiGraphics.renderItemDecorations(font, fileItem, slotX, slotY);
        }
    }

    private void drawPageButtons(GuiGraphics guiGraphics, int x, int y) {
        int btnY = y + 76;
        String pageText = (menu.getCurrentPage() + 1) + "/" + menu.getTotalPages();
        int textColor = 0xAAAAAA;

        // 上一页按钮
        if (menu.getCurrentPage() > 0) {
            guiGraphics.drawString(font, "◀ 上一页", x + 20, btnY, 0xFFFFFF);
        }

        // 页码
        int textWidth = font.width(pageText);
        guiGraphics.drawString(font, pageText, x + imageWidth / 2 - textWidth / 2, btnY, textColor);

        // 下一页按钮
        if (menu.getCurrentPage() < menu.getTotalPages() - 1) {
            String nextText = "下一页 ▶";
            guiGraphics.drawString(font, nextText, x + imageWidth - 20 - font.width(nextText), btnY, 0xFFFFFF);
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
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index < 27) {
            List<Path> pageFiles = this.menu.getPageFiles();
            int idx = this.hoveredSlot.index;
            if (idx >= pageFiles.size()) {
                super.renderTooltip(guiGraphics, mouseX, mouseY);
                return;
            }

            String structureName = getFileNameWithoutExtension(pageFiles.get(idx));
            Map<String, Integer> materials = materialsCache.computeIfAbsent(
                    this.menu.getCurrentPage() * 27 + idx, i -> {
                        return StructureFileHelper.loadMaterials(structureName);
                    });

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("§6§l" + structureName));
            tooltip.add(Component.literal("§7§l所需材料:"));

            if (materials != null && !materials.isEmpty()) {
                materials.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .forEach(entry -> {
                            String displayName = getBlockDisplayName(entry.getKey());
                            tooltip.add(Component.literal(" §7• " + displayName + " §fx" + entry.getValue()));
                        });
            } else {
                tooltip.add(Component.literal(" §7无材料数据"));
            }

            guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            return;
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private String getBlockDisplayName(String blockId) {
        ResourceLocation rl = ResourceLocation.tryParse(blockId);
        if (rl != null) {
            Block block = ForgeRegistries.BLOCKS.getValue(rl);
            if (block != null) {
                return block.getName().getString();
            }
        }
        return blockId;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 检查页按钮点击
        int btnY = y + 76;
        if (button == 0) {
            // 上一页
            if (menu.getCurrentPage() > 0) {
                int prevTextWidth = font.width("◀ 上一页");
                if (mouseX >= x + 20 && mouseX <= x + 20 + prevTextWidth &&
                        mouseY >= btnY && mouseY <= btnY + 10) {
                    menu.goToPage(menu.getCurrentPage() - 1);
                    materialsCache.clear();
                    return true;
                }
            }
            // 下一页
            if (menu.getCurrentPage() < menu.getTotalPages() - 1) {
                String nextText = "下一页 ▶";
                int nextTextWidth = font.width(nextText);
                int nextX = x + imageWidth - 20 - nextTextWidth;
                if (mouseX >= nextX && mouseX <= nextX + nextTextWidth &&
                        mouseY >= btnY && mouseY <= btnY + 10) {
                    menu.goToPage(menu.getCurrentPage() + 1);
                    materialsCache.clear();
                    return true;
                }
            }
        }

        // 检查结构槽位点击
        List<Path> pageFiles = menu.getPageFiles();
        for (int i = 0; i < pageFiles.size(); i++) {
            int slotX = x + 8 + (i % 9) * 18;
            int slotY = y + 17 + (i / 9) * 18;

            if (mouseX >= slotX && mouseX <= slotX + 16 &&
                    mouseY >= slotY && mouseY <= slotY + 16) {
                menu.clicked(i, button, net.minecraft.world.inventory.ClickType.PICKUP, minecraft.player);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}