package com.soranimi404.buildshare.block.entity;

import com.soranimi404.buildshare.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ImportBlockEntity extends BlockEntity {
    private final Map<String, Integer> submitted = new HashMap<>();
    private String structureName = null;

    public ImportBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IMPORT_BLOCK_ENTITY.get(), pos, state);
    }

    public void setStructureName(String name) {
        this.structureName = name;
        this.submitted.clear();
    }

    public String getStructureName() {
        return structureName;
    }

    public void submitItem(ItemStack stack) {
        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        submitted.merge(id, stack.getCount(), Integer::sum);
        stack.shrink(stack.getCount());
    }

    public boolean isReadyToBuild(Map<String, Integer> required) {
        for (Map.Entry<String, Integer> e : required.entrySet()) {
            int have = submitted.getOrDefault(e.getKey(), 0);
            if (have < e.getValue()) return false;
        }
        return true;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        structureName = tag.getString("structure");
        submitted.clear();
        CompoundTag sub = tag.getCompound("submitted");
        for (String key : sub.getAllKeys()) {
            submitted.put(key, sub.getInt(key));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("structure", structureName == null ? "" : structureName);
        CompoundTag sub = new CompoundTag();
        submitted.forEach(sub::putInt);
        tag.put("submitted", sub);
    }
}
