package com.soranimi404.buildshare.block;

import com.soranimi404.buildshare.menu.StructureSelectionMenu;
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

public class ImportBlock extends Block {

    public ImportBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 打开结构选择菜单
            MenuProvider menuProvider = new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) ->
                            new StructureSelectionMenu(containerId, playerInventory),
                    Component.literal("选择要导入的建筑")
            );

            NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> buf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}