package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.util.StructureExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExportNamePacket {
    private final BlockPos corner1;
    private final BlockPos corner2;
    private final String name;

    public ExportNamePacket(BlockPos corner1, BlockPos corner2, String name) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.name = name;
    }

    public ExportNamePacket(FriendlyByteBuf buf) {
        this.corner1 = buf.readBlockPos();
        this.corner2 = buf.readBlockPos();
        this.name = buf.readUtf(50);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(corner1);
        buf.writeBlockPos(corner2);
        buf.writeUtf(name);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 在服务端执行导出
            StructureExporter.exportStructure(
                    ctx.get().getSender(),
                    corner1,
                    corner2,
                    name
            );
        });
        ctx.get().setPacketHandled(true);
    }
}