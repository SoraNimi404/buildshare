package com.soranimi404.buildshare.network;

import com.soranimi404.buildshare.BuildShareMod;
import com.soranimi404.buildshare.menu.MaterialSubmitMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("buildshare", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, GenerateStructurePacket.class,
                GenerateStructurePacket::encode,
                GenerateStructurePacket::new,
                PacketHandler::handleGenerateStructure);
    }

    private static void handleGenerateStructure(GenerateStructurePacket packet,
                                                Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof MaterialSubmitMenu menu) {
                menu.generateStructure(packet.pos);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static class GenerateStructurePacket {
        private final BlockPos pos;

        public GenerateStructurePacket(BlockPos pos) {
            this.pos = pos;
        }

        public GenerateStructurePacket(FriendlyByteBuf buf) {
            this.pos = buf.readBlockPos();
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(pos);
        }
    }
}