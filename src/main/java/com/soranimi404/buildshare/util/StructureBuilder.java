package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StructureBuilder {

    // 生成建筑
    public static void buildStructure(Level level, BlockPos origin, BuildShareData.StructureCapture capture) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 按Y坐标排序（从下到上生成）
        List<Map.Entry<BlockPos, BlockState>> sortedBlocks = new ArrayList<>(capture.blocks.entrySet());
        sortedBlocks.sort(Comparator.comparingInt(entry -> entry.getKey().getY()));

        int blocksPlaced = 0;
        int totalBlocks = sortedBlocks.size();

        for (Map.Entry<BlockPos, BlockState> entry : sortedBlocks) {
            BlockPos relativePos = entry.getKey();
            BlockState state = entry.getValue();
            BlockPos worldPos = origin.offset(relativePos.getX(), relativePos.getY(), relativePos.getZ());

            // 设置方块状态
            level.setBlock(worldPos, state, Block.UPDATE_ALL);

            // 设置方块实体
            if (capture.tileEntities.containsKey(relativePos)) {
                CompoundTag nbt = capture.tileEntities.get(relativePos);
                BlockEntity blockEntity = level.getBlockEntity(worldPos);
                if (blockEntity != null) {
                    blockEntity.load(nbt);
                }
            }

            blocksPlaced++;

            // 每放置10个方块发送一次进度更新
            if (blocksPlaced % 10 == 0) {
                int progress = (int) ((float) blocksPlaced / totalBlocks * 100);
                level.players().forEach(player ->
                        player.displayClientMessage(Component.literal(
                                "§a生成中... " + progress + "%"), true)
                );
            }
        }

        // 生成完成消息
        level.players().forEach(player ->
                player.displayClientMessage(Component.literal(
                        "§a建筑生成完成! 共生成 " + blocksPlaced + " 个方块"), true)
        );
    }
}