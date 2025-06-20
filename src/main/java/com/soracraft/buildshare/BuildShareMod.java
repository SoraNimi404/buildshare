// BuildShareMod.java

package com.soracraft.buildshare;

import com.soracraft.buildshare.init.ModBlocks;
import com.soracraft.buildshare.init.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("buildshare")
public class BuildShareMod {
    public BuildShareMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
    }
}
