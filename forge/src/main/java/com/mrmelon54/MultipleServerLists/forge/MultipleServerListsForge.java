package com.mrmelon54.MultipleServerLists.forge;

import dev.architectury.platform.forge.EventBuses;
import com.mrmelon54.MultipleServerLists.MultipleServerLists;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MultipleServerLists.MOD_ID)
public class MultipleServerListsForge {
    public MultipleServerListsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(MultipleServerLists.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> MultipleServerLists.createConfigScreen(screen)));
        MultipleServerLists.init();
    }
}
