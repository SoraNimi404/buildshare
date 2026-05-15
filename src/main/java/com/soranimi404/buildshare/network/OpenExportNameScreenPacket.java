package com.soranimi404.buildshare.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenExportNameScreenPacket {
    private final BlockPos corner1;
    private final BlockPos corner2;

    public OpenExportNameScreenPacket(BlockPos corner1, BlockPos corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public OpenExportNameScreenPacket(FriendlyByteBuf buf) {
        this.corner1 = buf.readBlockPos();
        this.corner2 = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(corner1);
        buf.writeBlockPos(corner2);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 在客户端打开界面
            com.soranimi404.buildshare.client.screen.ExportNameScreen.open(corner1, corner2);
        });
        ctx.get().setPacketHandled(true);
    }
}