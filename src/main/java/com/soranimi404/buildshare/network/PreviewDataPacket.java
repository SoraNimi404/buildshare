package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.client.ClientPreviewData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PreviewDataPacket {
    private final BlockPos anchorPos;
    private final Map<BlockPos, BlockState> blocks;

    public PreviewDataPacket(BlockPos anchorPos, Map<BlockPos, BlockState> blocks) {
        this.anchorPos = anchorPos;
        this.blocks = blocks;
    }

    public PreviewDataPacket(FriendlyByteBuf buf) {
        this.anchorPos = buf.readBlockPos();
        int size = buf.readVarInt();
        this.blocks = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            BlockPos pos = buf.readBlockPos();
            int stateId = buf.readVarInt();
            BlockState state = Block.stateById(stateId);
            blocks.put(pos, state);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(anchorPos);
        buf.writeVarInt(blocks.size());
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            buf.writeVarInt(Block.getId(entry.getValue()));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPreviewData.set(anchorPos, blocks))
        );
        ctx.get().setPacketHandled(true);
    }
}
