package com.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.multiplayer.ServerList;

public interface MultiplayerScreenDuckProvider {
    ServerList multiple_server_lists$getServerListForTab(int tab);

    int multiple_server_lists$getCurrentTab();

    void multiple_server_lists$setCurrentTab(int currentTab);
}
