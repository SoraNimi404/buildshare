package com.soranimi404.buildshare.item;

import com.soranimi404.buildshare.init.ModBlocks;
import com.soranimi404.buildshare.util.StructureExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;

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

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (state.getBlock() == ModBlocks.EXPORT_MARKER.get()) {

                StructureExporter.handleMarkerPlacement(player, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }
}