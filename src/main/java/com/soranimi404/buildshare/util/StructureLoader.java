package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureLoader {

    // 列出所有结构文件
    public static List<Path> listStructureFiles() {
        List<Path> files = new ArrayList<>();
        Path dir = Paths.get("buildshare", "structures");

        if (Files.exists(dir)) {
            try (var stream = Files.newDirectoryStream(dir, "*.nbt")) {
                stream.forEach(files::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    // 加载结构元数据
    public static BuildShareData.StructureMetadata loadMetadata(Path file) {
        try {
            CompoundTag nbt = NbtIo.readCompressed(file.toFile());
            CompoundTag metadata = nbt.getCompound("metadata");

            BuildShareData.StructureMetadata meta = new BuildShareData.StructureMetadata();
            meta.name = metadata.getString("name");
            meta.author = metadata.getString("author");
            meta.created = metadata.getLong("created");
            meta.size = metadata.getIntArray("size");

            return meta;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 加载材料清单
    public static Map<String, Integer> loadMaterials(Path nbtFile) {
        Path materialsFile = Paths.get(nbtFile.toString().replace(".nbt", "_materials.json"));

        try {
            String json = Files.readString(materialsFile);
            CompoundTag materialsTag = JsonToNBT.parseTag(json);

            Map<String, Integer> materials = new HashMap<>();
            for (String key : materialsTag.getAllKeys()) {
                materials.put(key, materialsTag.getInt(key));
            }

            return materials;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // 加载完整结构
    public static BuildShareData.StructureCapture loadStructure(Path file) {
        try {
            CompoundTag root = NbtIo.readCompressed(file.toFile());
            BuildShareData.StructureCapture capture = new BuildShareData.StructureCapture();

            // 加载元数据
            CompoundTag metadata = root.getCompound("metadata");
            capture.metadata.name = metadata.getString("name");
            capture.metadata.author = metadata.getString("author");
            capture.metadata.created = metadata.getLong("created");
            capture.metadata.size = metadata.getIntArray("size");

            // 加载调色板
            ListTag paletteTag = root.getList("palette", 10); // 10 = CompoundTag ID
            for (int i = 0; i < paletteTag.size(); i++) {
                CompoundTag stateTag = paletteTag.getCompound(i);
                BlockState state = NbtUtils.readBlockState(stateTag);
                capture.palette.put(state, i);
            }

            // 加载方块
            ListTag blocksTag = root.getList("blocks", 10);
            for (int i = 0; i < blocksTag.size(); i++) {
                CompoundTag blockTag = blocksTag.getCompound(i);
                int[] pos = blockTag.getIntArray("pos");
                BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);

                int stateIndex = blockTag.getInt("state");
                BlockState state = capture.palette.keySet().stream()
                        .filter(s -> capture.palette.get(s) == stateIndex)
                        .findFirst().orElse(null);

                if (state != null) {
                    capture.blocks.put(blockPos, state);

                    if (blockTag.contains("nbt")) {
                        capture.tileEntities.put(blockPos, blockTag.getCompound("nbt"));
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