package com.soranimi404.buildshare.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.parse("buildshare:main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        // 注册打开界面包
        INSTANCE.registerMessage(
                id++,
                OpenExportNameScreenPacket.class,
                OpenExportNameScreenPacket::encode,
                OpenExportNameScreenPacket::new,
                OpenExportNameScreenPacket::handle
        );

        // 注册导出名称包
        INSTANCE.registerMessage(
                id++,
                ExportNamePacket.class,
                ExportNamePacket::encode,
                ExportNamePacket::new,
                ExportNamePacket::handle
        );
    }
}