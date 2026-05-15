package com.soranimi404.buildshare.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ClientPreviewData {
    private static final Map<BlockPos, BlockState> previewBlocks = new HashMap<>();
    private static BlockPos anchorPos = BlockPos.ZERO;
    private static boolean active = false;

    public static void set(BlockPos anchor, Map<BlockPos, BlockState> blocks) {
        previewBlocks.clear();
        previewBlocks.putAll(blocks);
        anchorPos = anchor;
        active = true;
    }

    public static void clear() {
        previewBlocks.clear();
        anchorPos = BlockPos.ZERO;
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static BlockPos getAnchorPos() {
        return anchorPos;
    }

    public static Map<BlockPos, BlockState> getBlocks() {
        return previewBlocks;
    }
}
