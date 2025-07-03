package com.soranimi404.buildshare.menu;

import com.soranimi404.buildshare.data.BuildShareData;
import com.soranimi404.buildshare.init.ModMenus;
import com.soranimi404.buildshare.util.StructureBuilder;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MaterialSubmitMenu extends AbstractContainerMenu {

    private final Player player;
    private final Path structureFile;
    public final Map<String, Integer> requiredMaterials;
    public final Map<String, Integer> submittedMaterials = new HashMap<>();

    public static void open(ServerPlayer player, Path structureFile) {
        NetworkHooks.openScreen(player,
                new SimpleMenuProvider(
                        (containerId, playerInventory, playerEntity) ->
                                new MaterialSubmitMenu(containerId, playerInventory, structureFile),
                        Component.literal("提交材料")
                ),
                buf -> buf.writeUtf(structureFile.toString())
        );
    }

    public MaterialSubmitMenu(int containerId, Inventory playerInventory, Path structureFile) {
        super(ModMenus.MATERIAL_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.structureFile = structureFile;
        this.requiredMaterials = StructureLoader.loadMaterials(structureFile);

        // 材料提交槽位 (3x3网格)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = row * 3 + col;
                this.addSlot(new MaterialSlot(new ItemStackHandler(9), index, 8 + col * 18, 20 + row * 18));
            }
        }

        // 玩家物品栏
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 玩家快捷栏
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // 只允许从玩家物品栏移动到提交槽位
        if (index >= 9 && index < 45) {
            Slot slot = this.slots.get(index);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                for (int i = 0; i < 9; i++) {
                    Slot submitSlot = this.slots.get(i);
                    if (!submitSlot.hasItem()) {
                        submitSlot.set(stack.split(1));
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // 当玩家提交材料时
    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);

        // 更新提交的材料
        submittedMaterials.clear();
        for (int i = 0; i < 9; i++) {
            Slot slot = this.slots.get(i);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem();
                String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
                submittedMaterials.merge(itemId, 1, Integer::sum);
            }
        }

        // 检查是否可以生成
        if (canGenerate()) {
            // 显示生成按钮
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.literal("§a材料已足够，点击生成按钮!"), true);
            }
        }
    }

    // 检查材料是否足够
    private boolean canGenerate() {
        if (player.isCreative()) return true;

        for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
            int submitted = submittedMaterials.getOrDefault(entry.getKey(), 0);
            if (submitted < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // 生成建筑
    public void generateStructure(BlockPos origin) {
        if (!canGenerate()) {
            return;
        }

        BuildShareData.StructureCapture capture = StructureLoader.loadStructure(structureFile);
        if (capture != null) {
            StructureBuilder.buildStructure(player.level(), origin, capture);

            // 消耗材料（生存模式）
            if (!player.isCreative()) {
                for (int i = 0; i < 9; i++) {
                    Slot slot = this.slots.get(i);
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
    }

    // 自定义材料槽位
    private static class MaterialSlot extends SlotItemHandler {
        public MaterialSlot(ItemStackHandler itemHandler, int index, int x, int y) {
            super(itemHandler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // 只允许放置建筑需要的材料
            String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
            return requiredMaterials.containsKey(itemId);
        }
    }
}