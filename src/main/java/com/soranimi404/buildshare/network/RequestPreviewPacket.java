package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.util.ServerPreviewManager;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RequestPreviewPacket {
    private final String fileName;
    private final BlockPos blockPos;

    public RequestPreviewPacket(String fileName, BlockPos blockPos) {
        this.fileName = fileName;
        this.blockPos = blockPos;
    }

    public RequestPreviewPacket(FriendlyByteBuf buf) {
        this.fileName = buf.readUtf(256);
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(fileName);
        buf.writeBlockPos(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            Path file = Paths.get("buildshare", "structures", fileName + ".nbt");
            BuildShareData.StructureCapture capture = StructureLoader.loadStructure(file);
            if (capture == null || capture.blocks.isEmpty()) {
                player.displayClientMessage(Component.literal("§c加载建筑数据失败！"), true);
                return;
            }

            // 存储服务端预览会话
            ServerPreviewManager.startPreview(player.getUUID(), blockPos, fileName);

            // 构建方块列表 (世界坐标)
            Map<BlockPos, BlockState> worldBlocks = new HashMap<>();
            for (Map.Entry<BlockPos, BlockState> entry : capture.blocks.entrySet()) {
                BlockPos worldPos = blockPos.offset(entry.getKey());
                worldBlocks.put(worldPos, entry.getValue());
            }

            // 发送预览数据给客户端
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PreviewDataPacket(blockPos, worldBlocks)
            );

            player.displayClientMessage(Component.literal("§a预览模式已开启 - 右击导入方块确认建造，左击导入方块取消"), true);
        });
        ctx.get().setPacketHandled(true);
    }
}
