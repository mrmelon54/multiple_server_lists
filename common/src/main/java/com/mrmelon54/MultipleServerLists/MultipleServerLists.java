package com.mrmelon54.MultipleServerLists;

import com.mrmelon54.MultipleServerLists.config.ConfigStructure;
import com.mrmelon54.MultipleServerLists.duck.MultiplayerScreenDuckProvider;
import com.mrmelon54.MultipleServerLists.util.CustomFileServerList;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MultipleServerLists {
    public static final Logger LOGGER = LoggerFactory.getLogger(MultipleServerLists.class);
    public static final String MOD_ID = "multiple_server_lists";
    private static final Pattern pattern = Pattern.compile("servers([0-9]+)\\.dat");
    private static MultiplayerScreenDuckProvider multiplayerScreenDuckProvider;
    public static ConfigStructure config;

    public static void init() {
        AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ConfigStructure.class).getConfig();
    }

    public static Screen createConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(ConfigStructure.class, parent).get();
    }

    public static List<Integer> getTabList() {
        List<Integer> availableTabs = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        File s760nbt = new File(mc.gameDirectory, "s760");
        if (s760nbt.exists()) {
            Predicate<String> patternPredicate = pattern.asMatchPredicate();
            File[] files = s760nbt.listFiles((dir, name) -> patternPredicate.test(name));
            if (files != null) {
                for (File file : files) {
                    final Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find()) {
                        try {
                            availableTabs.add(Integer.parseInt(matcher.group(1)));
                        } catch (NumberFormatException ignored) {
                            LOGGER.error("The number " + matcher.group(1) + " was unable to be read so the file was not included in the merged vanilla server lists");
                            LOGGER.error("If this resulted in missing servers then please inform the developer via Discord, CurseForge or GitHub");
                        }
                    }
                }
            }
            Collections.sort(availableTabs);
        }
        return availableTabs;
    }

    public static List<CustomFileServerList> getTabServerList() {
        Minecraft mc = Minecraft.getInstance();
        List<Integer> tabList = getTabList();
        return tabList.stream().map(n -> new CustomFileServerList(mc, n)).collect(Collectors.toList());
    }

    public static void safeUninstallForVanilla() {
        Minecraft mc = Minecraft.getInstance();
        List<Integer> tabList = getTabList();
        ServerList mainTab = getServerListForTab(mc, 0);
        if (mainTab == null) {
            LOGGER.error("Failed to load server list for main tab");
            return;
        }
        mainTab.load();
        for (int a : tabList) {
            ServerList currentTab = getServerListForTab(mc, a);
            if (currentTab == null) {
                LOGGER.error("Failed to load server list for tab " + a);
                return;
            }
            currentTab.load();
            for (int i = 0; i < currentTab.size(); i++) mainTab.add(currentTab.get(i), false);
        }
        mainTab.save();

        // wait until after saving
        for (int a : tabList)
            if (getServerListForTab(mc, a) instanceof CustomFileServerList customFileServerList)
                customFileServerList.deleteFile();
    }

    public static ServerList getServerListForTab(Minecraft mc, int tab) {
        if (tab < 0) return null;
        return tab == 0 ? new ServerList(mc) : new CustomFileServerList(mc, tab);
    }

    public static void setMultiplayerScreen(MultiplayerScreenDuckProvider duck) {
        multiplayerScreenDuckProvider = duck;
    }

    public static void setTab(int n) {
        if (multiplayerScreenDuckProvider != null) multiplayerScreenDuckProvider.multiple_server_lists$setCurrentTab(n);
    }

    public static int getTab() {
        return multiplayerScreenDuckProvider != null ? multiplayerScreenDuckProvider.multiple_server_lists$getCurrentTab() : 0;
    }
}
