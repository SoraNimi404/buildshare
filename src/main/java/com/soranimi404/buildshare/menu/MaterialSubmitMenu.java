package com.soranimi404.buildshare.menu;

import com.soranimi404.buildshare.init.ModMenus;
import com.soranimi404.buildshare.util.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkHooks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MaterialSubmitMenu extends AbstractContainerMenu {

    private final Player player;
    private final BlockPos blockPos; // 存储位置信息
    private final Path structureFile;
    public final Map<String, Integer> requiredMaterials;
    public final Map<String, Integer> submittedMaterials = new HashMap<>();
    private final ItemStackHandler materialSlots = new ItemStackHandler(9);

    public static void open(ServerPlayer player, BlockPos blockPos, String fileName) {
        NetworkHooks.openScreen(player,
                new SimpleMenuProvider(
                        (containerId, playerInventory, playerEntity) ->
                                new MaterialSubmitMenu(containerId, playerInventory, blockPos, fileName),
                        Component.literal("提交材料")
                ),
                buf -> {
                    buf.writeBlockPos(blockPos);
                    buf.writeUtf(fileName);
                }
        );
    }

    public MaterialSubmitMenu(int containerId, Inventory playerInventory, BlockPos blockPos, String fileName) {
        super(ModMenus.MATERIAL_SUBMIT_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.blockPos = blockPos; // 存储位置信息
        this.structureFile = Paths.get("buildshare", "structures", fileName);
        this.requiredMaterials = StructureLoader.loadMaterials(this.structureFile);

        // 材料提交槽位 (3x3网格)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = row * 3 + col;
                this.addSlot(new SlotItemHandler(materialSlots, index, 30 + col * 18, 17 + row * 18));
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

    public MaterialSubmitMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, data.readBlockPos(), data.readUtf());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // 只允许从玩家物品栏移动到提交槽位
        if (index >= 9 && index < 45) {
            Slot slot = this.slots.get(index);
            if (slot.hasItem()) {
                ItemStack stack = slot.getItem().copy();
                stack.setCount(1);

                // 尝试放入第一个空槽
                for (int i = 0; i < 9; i++) {
                    if (materialSlots.getStackInSlot(i).isEmpty()) {
                        materialSlots.setStackInSlot(i, stack);
                        slot.remove(1);
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

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        super.slotsChanged(container);
        updateMaterials();
    }

    private void updateMaterials() {
        // 更新提交的材料
        submittedMaterials.clear();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = materialSlots.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // 使用新的注册表系统获取物品ID
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (itemId != null) {
                    submittedMaterials.merge(itemId.toString(), 1, Integer::sum);
                }
            }
        }
    }

    // 检查材料是否足够
    public boolean canGenerate() {
        if (player.isCreative()) {
            return true;
        }

        for (Map.Entry<String, Integer> entry : requiredMaterials.entrySet()) {
            int submitted = submittedMaterials.getOrDefault(entry.getKey(), 0);
            if (submitted < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // 生成建筑 - 使用菜单中存储的位置
    public void generateStructure() {
        generateStructure(this.blockPos);
    }

    // 生成建筑 - 使用传入的位置参数
    public void generateStructure(BlockPos pos) {
        if (!canGenerate()) {
            player.displayClientMessage(Component.literal("§c材料不足，无法生成建筑！"), true);
            return;
        }

        // 在实际项目中，这里会调用StructureBuilder
        player.displayClientMessage(Component.literal("§a建筑生成成功! 位置: " + pos), true);

        // 消耗材料（生存模式）
        if (!player.isCreative()) {
            for (int i = 0; i < 9; i++) {
                materialSlots.setStackInSlot(i, ItemStack.EMPTY);
            }
            updateMaterials(); // 更新材料状态
        }
    }

    // 获取位置信息（可选）
    public BlockPos getBlockPos() {
        return blockPos;
    }
}