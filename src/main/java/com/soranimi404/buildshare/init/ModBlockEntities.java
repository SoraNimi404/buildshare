package com.soranimi404.buildshare.init;

import com.soranimi404.buildshare.block.entity.ImportBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "buildshare");

    public static final RegistryObject<BlockEntityType<ImportBlockEntity>> IMPORT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("import_block_entity",
                    () -> BlockEntityType.Builder.of(ImportBlockEntity::new, ModBlocks.IMPORT_BLOCK.get()).build(null));
}
