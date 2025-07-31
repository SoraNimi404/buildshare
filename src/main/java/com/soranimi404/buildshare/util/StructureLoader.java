package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StructureLoader {

    // 列出所有结构文件
    public static List<Path> listStructureFiles() {
        try {
            Path dir = Paths.get("buildshare", "structures");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                return Collections.emptyList();
            }

            return Files.list(dir)
                    .filter(path -> path.toString().endsWith(".nbt"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 加载完整结构
    public static BuildShareData.StructureCapture loadStructure(Path file) {
        try {
            CompoundTag root = NbtIo.readCompressed(file.toFile());
            BuildShareData.StructureCapture capture = new BuildShareData.StructureCapture();

            // 获取方块注册表的HolderGetter
            HolderLookup<Block> blockLookup = BuiltInRegistries.BLOCK.asLookup();
            HolderGetter<Block> blockGetter = blockLookup;

            // 加载元数据
            if (root.contains("metadata", Tag.TAG_COMPOUND)) {
                CompoundTag metadata = root.getCompound("metadata");
                capture.metadata.name = metadata.getString("name");
                capture.metadata.author = metadata.getString("author");
                capture.metadata.created = metadata.getLong("created");
                capture.metadata.size = metadata.getIntArray("size");
            }

            // 加载调色板
            if (root.contains("palette", Tag.TAG_LIST)) {
                ListTag paletteTag = root.getList("palette", Tag.TAG_COMPOUND);
                for (int i = 0; i < paletteTag.size(); i++) {
                    BlockState state = NbtUtils.readBlockState(blockGetter, paletteTag.getCompound(i));
                    capture.palette.put(state, i);
                }
            }

            // 加载方块
            if (root.contains("blocks", Tag.TAG_LIST)) {
                ListTag blocksTag = root.getList("blocks", Tag.TAG_COMPOUND);
                for (int i = 0; i < blocksTag.size(); i++) {
                    CompoundTag blockTag = blocksTag.getCompound(i);

                    // 读取位置
                    int[] posArray = blockTag.getIntArray("pos");
                    BlockPos pos = new BlockPos(posArray[0], posArray[1], posArray[2]);

                    // 读取方块状态
                    int stateIndex = blockTag.getInt("state");
                    BlockState state = null;

                    // 通过索引查找状态
                    if (stateIndex >= 0 && stateIndex < capture.palette.size()) {
                        state = (BlockState) capture.palette.keySet().toArray()[stateIndex];
                    }

                    if (state != null) {
                        capture.blocks.put(pos, state);

                        // 加载方块实体数据
                        if (blockTag.contains("nbt", Tag.TAG_COMPOUND)) {
                            capture.tileEntities.put(pos, blockTag.getCompound("nbt"));
                        }
                    }
                }
            }

            return capture;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}