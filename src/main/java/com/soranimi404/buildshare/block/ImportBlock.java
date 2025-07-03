package com.soranimi404.buildshare.block;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
import net.minecraft.ChatFormatting;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImportBlock extends Block {

    private static final Logger LOGGER = LogManager.getLogger();

    public ImportBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {


                try {
                    MenuProvider menuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory, playerEntity) -> {
                                // 在创建菜单时显示消息

                                return new StructureSelectionMenu(containerId, playerInventory);
                            },
                            Component.literal("选择要导入的建筑")
                    );

                    // 显示网络调用消息


                    NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> buf.writeBlockPos(pos));



                    return InteractionResult.SUCCESS;
                } catch (Exception e) {
                    // 错误消息
                    player.displayClientMessage(
                            Component.literal("§c打开菜单时出错: " + e.getMessage())
                                    .withStyle(ChatFormatting.RED),
                            false
                    );
                    return InteractionResult.FAIL;
                }
            } else {
                player.displayClientMessage(
                        Component.literal("§c玩家不是ServerPlayer实例")
                                .withStyle(ChatFormatting.RED),
                        false
                );
            }
        } else {
            player.displayClientMessage(
                    Component.literal("§e客户端交互")
                            .withStyle(ChatFormatting.YELLOW),
                    false
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}