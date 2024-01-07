package com.mrmelon54.MultipleServerLists.fabriclike;

import com.mrmelon54.MultipleServerLists.MultipleServerLists;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MultipleServerLists::createConfigScreen;
    }
}
