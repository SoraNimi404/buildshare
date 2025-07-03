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

    // 生成建筑
    public static void buildStructure(Level level, BlockPos origin, BuildShareData.StructureCapture capture) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // 按Y坐标排序（从下到上生成）
        List<Map.Entry<BlockPos, BlockState>> sortedBlocks = new ArrayList<>(capture.blocks.entrySet());
        sortedBlocks.sort(Comparator.comparingInt(entry -> entry.getKey().getY()));

        for (Map.Entry<BlockPos, BlockState> entry : sortedBlocks) {
            BlockPos relativePos = entry.getKey();
            BlockState state = entry.getValue();
            BlockPos worldPos = origin.offset(relativePos.getX(), relativePos.getY(), relativePos.getZ());

            // 设置方块状态
            level.setBlock(worldPos, state, 3);

            // 设置方块实体
            if (capture.tileEntities.containsKey(relativePos)) {
                BlockEntity blockEntity = level.getBlockEntity(worldPos);
                if (blockEntity != null) {
                    blockEntity.load(capture.tileEntities.get(relativePos));
                }
            }
        }

        level.players().forEach(player ->
                player.displayClientMessage(Component.literal(
                        "§a建筑生成完成!"), true)
        );
    }
}