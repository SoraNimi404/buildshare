package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.block.entity.ImportBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SubmitMaterialPacket {
    private final BlockPos pos;

    public SubmitMaterialPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static SubmitMaterialPacket decode(FriendlyByteBuf buf) {
        return new SubmitMaterialPacket(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level().getBlockEntity(pos) instanceof ImportBlockEntity entity) {
                ItemStack held = player.getMainHandItem();
                entity.submitItem(held);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
