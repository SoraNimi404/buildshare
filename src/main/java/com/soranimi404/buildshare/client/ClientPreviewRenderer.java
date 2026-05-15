package com.soranimi404.buildshare.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientPreviewRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!ClientPreviewData.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        var camera = event.getCamera();

        // 强制所有方块渲染到 translucent 缓冲区，确保半透明融合
        MultiBufferSource translucentWrapper = renderType ->
                bufferSource.getBuffer(RenderType.translucent());

        RenderSystem.depthMask(false);

        for (Map.Entry<BlockPos, BlockState> entry : ClientPreviewData.getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            poseStack.pushPose();
            poseStack.translate(
                    pos.getX() - camera.getPosition().x,
                    pos.getY() - camera.getPosition().y,
                    pos.getZ() - camera.getPosition().z
            );

            mc.getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    translucentWrapper,
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }

        bufferSource.endBatch(RenderType.translucent());
        RenderSystem.depthMask(true);
    }
}
