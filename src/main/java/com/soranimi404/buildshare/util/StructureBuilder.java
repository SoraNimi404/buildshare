package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StructureBuilder {

    // 修改方法签名，接受 Level 参数
    public static void buildStructure(Level level, BlockPos origin, BuildShareData.StructureCapture capture) {
        // 检查是否是服务器端
        if (!(level instanceof ServerLevel serverLevel)) {
            // 如果不是服务器端，发送错误消息
            if (level.isClientSide) {
                level.players().forEach(player ->
                        player.displayClientMessage(Component.literal("§c只能在服务器端生成建筑！"), true)
                );
            }
            return;
        }

        // 按Y坐标排序（从下到上生成）
        List<Map.Entry<BlockPos, BlockState>> sortedBlocks = new ArrayList<>(capture.blocks.entrySet());
        sortedBlocks.sort(Comparator.comparingInt(entry -> entry.getKey().getY()));

        for (Map.Entry<BlockPos, BlockState> entry : sortedBlocks) {
            BlockPos relativePos = entry.getKey();
            BlockState state = entry.getValue();
            BlockPos worldPos = origin.offset(
                    relativePos.getX(),
                    relativePos.getY(),
                    relativePos.getZ()
            );

            // 设置方块状态
            serverLevel.setBlock(worldPos, state, 3);

            // 设置方块实体数据
            if (capture.tileEntities.containsKey(relativePos)) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(worldPos);
                if (blockEntity != null) {
                    blockEntity.load(capture.tileEntities.get(relativePos));
                }
            }
        }

        serverLevel.players().forEach(player ->
                player.displayClientMessage(Component.literal("§a建筑生成完成!"), true)
        );
    }
}