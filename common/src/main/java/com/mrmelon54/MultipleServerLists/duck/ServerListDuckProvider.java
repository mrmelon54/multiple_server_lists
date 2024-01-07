package com.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.multiplayer.ServerData;

import java.util.List;

public interface ServerListDuckProvider {
    List<ServerData> multiple_server_lists$getServers();

    List<ServerData> multiple_server_lists$getHiddenServers();
}
