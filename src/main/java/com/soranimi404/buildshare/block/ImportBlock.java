package com.soranimi404.buildshare.block;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import com.soranimi404.buildshare.util.ServerPreviewManager;
import com.soranimi404.buildshare.util.StructureBuilder;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportBlock extends Block {

    public ImportBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 检查是否有预览会话
            ServerPreviewManager.PreviewSession session = ServerPreviewManager.getPreview(player.getUUID());
            if (session != null) {
                // 确认建造
                Path file = Paths.get("buildshare", "structures", session.fileName + ".nbt");
                BuildShareData.StructureCapture capture = StructureLoader.loadStructure(file);
                if (capture != null) {
                    StructureBuilder.buildStructure(level, pos, capture);
                    player.displayClientMessage(Component.literal("§a建筑生成成功！"), true);
                } else {
                    player.displayClientMessage(Component.literal("§c加载建筑失败！"), true);
                }
                ServerPreviewManager.endPreview(player.getUUID());
                // 通知客户端清除预览
                com.soranimi404.buildshare.network.PacketHandler.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new com.soranimi404.buildshare.network.ClearPreviewPacket()
                );
                return InteractionResult.SUCCESS;
            }

            // 无预览 → 打开选择菜单
            MenuProvider menuProvider = new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) ->
                            new StructureSelectionMenu(containerId, playerInventory, pos),
                    Component.literal("建筑导入 - 左键建造 | 右键预览")
            );
            NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> buf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
