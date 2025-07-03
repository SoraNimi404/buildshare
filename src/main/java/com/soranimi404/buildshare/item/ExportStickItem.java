package com.soranimi404.buildshare.item;

import com.soranimi404.buildshare.init.ModBlocks;
import com.soranimi404.buildshare.util.StructureExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ExportStickItem extends Item {

    public ExportStickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        // 确保在服务器端执行
        if (!level.isClientSide && player != null) {
            // 检查是否点击了导出方块
            if (state.getBlock() == ModBlocks.EXPORT_MARKER.get()) {
                StructureExporter.handleMarkerPlacement(player, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}