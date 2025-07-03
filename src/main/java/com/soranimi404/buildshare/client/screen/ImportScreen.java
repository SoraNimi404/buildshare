package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.block.entity.ImportBlockEntity;
import com.soranimi404.buildshare.menu.ImportMenu;
import com.soranimi404.buildshare.network.PacketHandler;
import com.soranimi404.buildshare.network.SubmitMaterialPacket;
import com.soranimi404.buildshare.util.StructureFileHelper;
import com.soranimi404.buildshare.BuildShareMod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Map;

public class ImportScreen extends AbstractContainerScreen<ImportMenu> {

    private List<String> structures;
    private String selected = null;
    private Map<String, Integer> materials;

    private final SimpleChannel channel = PacketHandler.INSTANCE;

    public ImportScreen(ImportMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.structures = StructureFileHelper.getStructureNames();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        int x = leftPos + 10;
        int y = topPos + 20;
        for (int i = 0; i < structures.size(); i++) {
            String name = structures.get(i);
            this.addRenderableWidget(Button.builder(Component.literal(name), b -> {
                this.selected = name;
                this.materials = StructureFileHelper.loadMaterials(name);
                ImportBlockEntity entity = menu.getBlockEntity();
                if (entity != null) {
                    entity.setStructureName(name);
                }
            }).bounds(x, y + i * 20, 120, 18).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("提交物品"), b -> {
            if (selected != null) {
                channel.sendToServer(new SubmitMaterialPacket(menu.getPos()));
            }
        }).bounds(leftPos + 10, topPos + 130, 60, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("一键构建"), b -> {
            LocalPlayer player = minecraft.player;
            if (player != null && player.isCreative() && selected != null) {
                // 可调用 StructureBuilder 逻辑，你需完成对应 buildStructure()
            }
        }).bounds(leftPos + 80, topPos + 130, 80, 20).build());
    }

    @Override
    protected void renderBg(net.minecraft.client.gui.GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    protected void renderLabels(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        if (selected != null && materials != null) {
            int y = 10;
            graphics.drawString(font, "所需材料：", 140, y, 0xFFFFFF);
            for (Map.Entry<String, Integer> e : materials.entrySet()) {
                graphics.drawString(font, e.getKey() + " x" + e.getValue(), 140, y += 10, 0xAAAAAA);
            }
        }
    }
}
