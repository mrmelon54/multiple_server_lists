package com.mrmelon54.MultipleServerLists.mixin;

import com.mrmelon54.MultipleServerLists.duck.ServerListDuckProvider;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerList.class)
public class ServerListMixin implements ServerListDuckProvider {
    @Shadow
    @Final
    private List<ServerData> serverList;

    @Shadow
    @Final
    private List<ServerData> hiddenServerList;

    @Override
    public List<ServerData> multiple_server_lists$getServers() {
        return this.serverList;
    }

    @Override
    public List<ServerData> multiple_server_lists$getHiddenServers() {
        return this.hiddenServerList;
    }
}
