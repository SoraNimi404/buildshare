package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理结构导出逻辑
 */
public class StructureExporter {

    // 存储玩家选择的角落坐标
    private static final Map<Player, BlockPos> firstCornerMap = new ConcurrentHashMap<>();

    /**
     * 处理玩家放置/选择导出方块
     */
    public static void handleMarkerPlacement(Player player, BlockPos pos) {
        if (firstCornerMap.containsKey(player)) {
            // 第二个角落已选择，开始导出
            BlockPos firstCorner = firstCornerMap.get(player);
            exportStructure(player, firstCorner, pos);
            firstCornerMap.remove(player);
        } else {
            // 第一个角落
            firstCornerMap.put(player, pos);
            player.displayClientMessage(
                    Component.literal("§a第一个角落已设置! 请放置第二个导出方块"),
                    true
            );
        }
    }

    /**
     * 执行结构导出
     */
    private static void exportStructure(Player player, BlockPos corner1, BlockPos corner2) {
        Level level = player.level();

        if (!level.isClientSide) {
            // 捕获结构
            BuildShareData.StructureCapture capture =
                    BuildShareData.captureStructure(level, corner1, corner2);

            // 生成自定义名称（基于结构尺寸）
            String sizeDesc = String.format("%dx%dx%d",
                    capture.metadata.size[0],
                    capture.metadata.size[1],
                    capture.metadata.size[2]);

            // 保存文件
            Path savedPath = BuildShareData.saveStructureToFile(capture,
                    player.getName().getString() + "_" + sizeDesc);

            if (savedPath != null) {
                player.displayClientMessage(
                        Component.literal("§a结构已导出: " + savedPath.getFileName()),
                        true
                );

                // 显示材料信息
                int blockCount = capture.blocks.size();
                player.displayClientMessage(
                        Component.literal(String.format("§7包含 %d 个方块 (%d 种类型)",
                                blockCount, capture.palette.size())),
                        false
                );
            } else {
                player.displayClientMessage(
                        Component.literal("§c导出失败! 请检查日志"),
                        true
                );
            }
        }
    }

    /**
     * 当玩家退出时清理缓存
     */
    public static void onPlayerLogout(Player player) {
        firstCornerMap.remove(player);
    }
}