package com.soranimi404.buildshare.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class StructureFileHelper {
    public static List<String> getStructureNames() {
        Path dir = Paths.get("buildshare", "structures");
        if (!Files.exists(dir)) return Collections.emptyList();
        try {
            return Files.list(dir)
                    .filter(p -> p.toString().endsWith(".nbt"))
                    .map(p -> p.getFileName().toString().replace(".nbt", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static Map<String, Integer> loadMaterials(String name) {
        Path path = Paths.get("buildshare", "structures", name + "_materials.json");
        Map<String, Integer> result = new HashMap<>();
        try {
            String content = Files.readString(path);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : json.entrySet()) {
                result.put(e.getKey(), e.getValue().getAsInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
