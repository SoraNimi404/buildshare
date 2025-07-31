package com.soranimi404.buildshare.client.screen;

import com.soranimi404.buildshare.network.ExportNamePacket;
import com.soranimi404.buildshare.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ExportNameScreen extends Screen {
    private final BlockPos corner1;
    private final BlockPos corner2;
    private EditBox nameField;

    public ExportNameScreen(BlockPos corner1, BlockPos corner2) {
        super(Component.literal("自定义建筑名称"));
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    // 添加静态方法打开界面
    public static void open(BlockPos corner1, BlockPos corner2) {
        Minecraft.getInstance().setScreen(new ExportNameScreen(corner1, corner2));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 创建名称输入框
        this.nameField = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20, Component.literal(""));
        this.nameField.setMaxLength(50);
        this.nameField.setValue("我的建筑");
        this.addRenderableWidget(nameField);

        // 创建确认按钮
        Button confirmButton = Button.builder(Component.literal("确认导出"), button -> {
            String name = nameField.getValue().trim();
            PacketHandler.INSTANCE.sendToServer(new ExportNamePacket(corner1, corner2, name));
            this.onClose();
        }).bounds(centerX - 50, centerY + 10, 100, 20).build();

        this.addRenderableWidget(confirmButton);

        // 取消按钮
        Button cancelButton = Button.builder(Component.literal("取消"), button -> {
            this.onClose();
        }).bounds(centerX - 50, centerY + 40, 100, 20).build();

        this.addRenderableWidget(cancelButton);

        // 设置初始焦点
        this.setInitialFocus(nameField);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 绘制标题
        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                centerX,
                centerY - 50,
                0xFFFFFF
        );

        // 绘制说明
        guiGraphics.drawCenteredString(
                this.font,
                "§7为导出的建筑命名",
                centerX,
                centerY - 35,
                0xAAAAAA
        );

        // 绘制输入框
        nameField.render(guiGraphics, mouseX, mouseY, partialTicks);

        // 绘制其他组件
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}