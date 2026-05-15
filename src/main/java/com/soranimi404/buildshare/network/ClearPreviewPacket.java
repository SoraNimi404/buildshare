package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.client.ClientPreviewData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearPreviewPacket {

    public ClearPreviewPacket() {}

    public ClearPreviewPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPreviewData.clear())
        );
        ctx.get().setPacketHandled(true);
    }
}
