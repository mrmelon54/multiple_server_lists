package com.mrmelon54.MultipleServerLists.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "multiple-server-lists")
@Config.Gui.Background("minecraft:textures/block/purple_concrete_powder.png")
public class ConfigStructure implements ConfigData {
    public boolean showTabs = false;
}
