package com.soranimi404.buildshare.event;

import com.soranimi404.buildshare.block.ImportBlock;
import com.soranimi404.buildshare.network.ClearPreviewPacket;
import com.soranimi404.buildshare.network.PacketHandler;
import com.soranimi404.buildshare.util.ServerPreviewManager;
import com.soranimi404.buildshare.util.StructureExporter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        StructureExporter.onPlayerLogout(player);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();
        if (ServerPreviewManager.hasPreview(player.getUUID())) {
            if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ImportBlock) {
                event.setCanceled(true);
                ServerPreviewManager.endPreview(player.getUUID());
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new ClearPreviewPacket()
                    );
                    serverPlayer.displayClientMessage(Component.literal("§c预览已取消"), true);
                }
            }
        }
    }
}