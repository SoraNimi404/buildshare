package com.soranimi404.buildshare.util;

import com.soranimi404.buildshare.data.BuildShareData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 加载材料清单
    public static Map<String, Integer> loadMaterials(Path nbtFile) {
        try {
            Path materialsFile = Paths.get(nbtFile.toString().replace(".nbt", "_materials.json"));
            if (!Files.exists(materialsFile)) {
                return Collections.emptyMap();
            }

            String json = Files.readString(materialsFile);
            // 简化的JSON解析（实际项目应使用JSON库）
            Map<String, Integer> materials = new HashMap<>();
            json = json.replace("{", "").replace("}", "").replace("\"", "");
            String[] entries = json.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    materials.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
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

            // 实际项目中会加载更多数据...
            return capture;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}