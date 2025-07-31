package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.network.OpenExportNameScreenPacket;
import com.soranimi404.buildshare.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StructureExporter {

    private static final Map<UUID, BlockPos> firstCornerMap = new ConcurrentHashMap<>();

    public static void handleMarkerPlacement(Player player, BlockPos pos) {
        UUID playerId = player.getUUID();

        if (firstCornerMap.containsKey(playerId)) {
            BlockPos firstCorner = firstCornerMap.get(playerId);

            // 从地图中移除记录
            firstCornerMap.remove(playerId);

            // 如果是服务端玩家，发送打开界面包
            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new OpenExportNameScreenPacket(firstCorner, pos)
                );
            }
            // 如果是单人游戏，直接打开界面
            else if (player.level().isClientSide) {
                com.soranimi404.buildshare.client.screen.ExportNameScreen.open(firstCorner, pos);
            }
        } else {
            // 记录第一个角落
            firstCornerMap.put(playerId, pos);
            player.displayClientMessage(
                    Component.literal("§a第一个角落已设置! 请放置第二个导出方块"),
                    true
            );
        }
    }

    // 服务端导出方法
    public static void exportStructure(Player player, BlockPos corner1, BlockPos corner2, String customName) {
        Level level = player.level();

        if (!level.isClientSide) {
            BuildShareData.StructureCapture capture =
                    BuildShareData.captureStructure(level, corner1, corner2);

            if (capture == null) {
                player.displayClientMessage(Component.literal("§c无法捕获建筑结构!"), true);
                return;
            }

            String sizeDesc = String.format("%dx%dx%d",
                    capture.metadata.size[0],
                    capture.metadata.size[1],
                    capture.metadata.size[2]);

            // 使用自定义名称
            String fileName = (customName != null && !customName.isEmpty())
                    ? customName.replaceAll("[^a-zA-Z0-9_-]", "_") + "_" + sizeDesc
                    : "structure_" + sizeDesc;

            Path savedPath = BuildShareData.saveStructureToFile(capture, fileName);

            if (savedPath != null) {
                player.displayClientMessage(
                        Component.literal("§a结构已导出: " + fileName),
                        true
                );

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

    public static void onPlayerLogout(Player player) {
        firstCornerMap.remove(player.getUUID());
    }
}