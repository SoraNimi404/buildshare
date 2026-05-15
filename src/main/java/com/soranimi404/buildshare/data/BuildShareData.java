package com.soranimi404.buildshare.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 处理建筑结构的捕获、序列化和导出
 */
public class BuildShareData {

    // 结构元数据
    public static class StructureMetadata {
        public String name = "Unnamed";
        public String author = "Unknown";
        public long created = System.currentTimeMillis();
        public int[] size = new int[3]; // [width, height, depth]
    }

    // 完整结构数据
    public static class StructureCapture {
        public StructureMetadata metadata = new StructureMetadata();
        public Map<BlockState, Integer> palette = new LinkedHashMap<>();
        public Map<BlockPos, BlockState> blocks = new ConcurrentHashMap<>();
        public Map<BlockPos, CompoundTag> tileEntities = new ConcurrentHashMap<>();

        public CompoundTag toNbt() {
            CompoundTag root = new CompoundTag();

            // 添加元数据
            CompoundTag metaTag = new CompoundTag();
            metaTag.putString("name", metadata.name);
            metaTag.putString("author", metadata.author);
            metaTag.putLong("created", metadata.created);
            metaTag.putIntArray("size", metadata.size);
            root.put("metadata", metaTag);

            // 创建调色板
            ListTag paletteTag = new ListTag();
            for (BlockState state : palette.keySet()) {
                paletteTag.add(NbtUtils.writeBlockState(state));
            }
            root.put("palette", paletteTag);

            // 添加方块数据
            ListTag blocksTag = new ListTag();
            for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
                CompoundTag blockTag = new CompoundTag();

                // 位置
                int[] posArray = {entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()};
                blockTag.putIntArray("pos", posArray);

                // 状态索引
                blockTag.putInt("state", palette.get(entry.getValue()));

                // 方块实体数据
                if (tileEntities.containsKey(entry.getKey())) {
                    blockTag.put("nbt", tileEntities.get(entry.getKey()));
                }

                blocksTag.add(blockTag);
            }
            root.put("blocks", blocksTag);

            return root;
        }

        // 计算材料清单
        public CompoundTag calculateMaterials() {
            CompoundTag materialsTag = new CompoundTag();
            Map<String, Integer> materialCounts = new HashMap<>();

            for (BlockState state : blocks.values()) {
                String blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();
                materialCounts.put(blockId, materialCounts.getOrDefault(blockId, 0) + 1);
            }

            for (Map.Entry<String, Integer> entry : materialCounts.entrySet()) {
                materialsTag.putInt(entry.getKey(), entry.getValue());
            }

            return materialsTag;
        }
    }

    /**
     * 从世界捕获结构
     * @param level 世界对象
     * @param corner1 第一个角落坐标
     * @param corner2 第二个角落坐标
     * @return 捕获的结构数据
     */
    public static StructureCapture captureStructure(Level level, BlockPos corner1, BlockPos corner2) {
        StructureCapture capture = new StructureCapture();

        // 计算尺寸
        int minX = Math.min(corner1.getX(), corner2.getX());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        capture.metadata.size[0] = maxX - minX + 1;
        capture.metadata.size[1] = maxY - minY + 1;
        capture.metadata.size[2] = maxZ - minZ + 1;

        // 设置作者（如果可能）
        if (level.getServer() != null && level.getServer().getPlayerList().getPlayerCount() > 0) {
            capture.metadata.author = level.getServer().getPlayerList().getPlayers().get(0).getName().getString();
        }

        // 遍历区域捕获方块
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos worldPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(worldPos);

                    // 跳过空气方块
                    if (state.isAir()) {
                        continue;
                    }

                    // 计算相对位置
                    BlockPos relativePos = new BlockPos(
                            x - minX,
                            y - minY,
                            z - minZ
                    );

                    // 添加到调色板和方块列表
                    if (!capture.palette.containsKey(state)) {
                        capture.palette.put(state, capture.palette.size());
                    }
                    capture.blocks.put(relativePos, state);

                    // 捕获方块实体
                    BlockEntity blockEntity = level.getBlockEntity(worldPos);
                    if (blockEntity != null) {
                        CompoundTag nbt = blockEntity.saveWithId();
                        capture.tileEntities.put(relativePos, nbt);
                    }
                }
            }
        }

        return capture;
    }

    /**
     * 保存结构到文件
     * @param capture 捕获的结构数据
     * @param customName 自定义文件名（可选）
     * @return 保存的文件路径
     */
    public static Path saveStructureToFile(StructureCapture capture, String customName) {
        // 创建目录
        Path exportDir = Paths.get("buildshare", "structures");
        if (!Files.exists(exportDir)) {
            try {
                Files.createDirectories(exportDir);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // 生成文件名
        String fileName = customName != null ?
                customName.replaceAll("[^a-zA-Z0-9_-]", "_") + ".nbt" :
                "structure_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".nbt";

        Path nbtPath = exportDir.resolve(fileName);
        Path materialsPath = exportDir.resolve(fileName.replace(".nbt", "_materials.json"));

        try {
            // 保存主结构文件 (NBT)
            CompoundTag root = capture.toNbt();
            NbtIo.writeCompressed(root, nbtPath.toFile());

            // 保存材料文件 (JSON)
            CompoundTag materials = capture.calculateMaterials();
            Files.writeString(materialsPath, materials.toString());

            return nbtPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}