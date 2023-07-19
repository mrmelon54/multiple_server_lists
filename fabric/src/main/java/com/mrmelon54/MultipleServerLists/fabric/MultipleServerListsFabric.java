package com.mrmelon54.MultipleServerLists.fabric;

import com.mrmelon54.MultipleServerLists.fabriclike.MultipleServerListsFabricLike;
import net.fabricmc.api.ModInitializer;

public class MultipleServerListsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MultipleServerListsFabricLike.init();
    }
}
