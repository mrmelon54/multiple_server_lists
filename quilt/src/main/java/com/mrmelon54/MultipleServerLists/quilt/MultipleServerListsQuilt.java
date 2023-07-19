package com.mrmelon54.MultipleServerLists.quilt;

import com.mrmelon54.MultipleServerLists.fabriclike.MultipleServerListsFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class MultipleServerListsQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        MultipleServerListsFabricLike.init();
    }
}
