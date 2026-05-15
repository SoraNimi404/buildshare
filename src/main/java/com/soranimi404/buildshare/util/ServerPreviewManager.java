package com.soranimi404.buildshare.util;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerPreviewManager {
    private static final Map<UUID, PreviewSession> sessions = new HashMap<>();

    public static class PreviewSession {
        public final BlockPos blockPos;
        public final String fileName;

        PreviewSession(BlockPos blockPos, String fileName) {
            this.blockPos = blockPos;
            this.fileName = fileName;
        }
    }

    public static void startPreview(UUID playerId, BlockPos blockPos, String fileName) {
        sessions.put(playerId, new PreviewSession(blockPos, fileName));
    }

    public static PreviewSession getPreview(UUID playerId) {
        return sessions.get(playerId);
    }

    public static void endPreview(UUID playerId) {
        sessions.remove(playerId);
    }

    public static boolean hasPreview(UUID playerId) {
        return sessions.containsKey(playerId);
    }
}
