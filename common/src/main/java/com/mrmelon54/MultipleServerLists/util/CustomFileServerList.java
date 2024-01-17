package com.mrmelon54.MultipleServerLists.util;

import com.mrmelon54.MultipleServerLists.duck.ServerListDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomFileServerList extends ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private File serversWrapperFolder;
    private final Minecraft client;
    private final int pageIndex;
    private String listName;

    public CustomFileServerList(Minecraft client, int pageIndex) {
        super(client);
        this.client = client;
        this.pageIndex = pageIndex;
        this.listName = "Page " + pageIndex;
        this.load();
    }

    public int index() {
        return pageIndex;
    }

    List<ServerData> getInternalServers() {
        if (this instanceof ServerListDuckProvider duck) return duck.multiple_server_lists$getServers();
        return Lists.newArrayList();
    }

    List<ServerData> getInternalHiddenServers() {
        if (this instanceof ServerListDuckProvider duck) return duck.multiple_server_lists$getHiddenServers();
        return Lists.newArrayList();
    }

    public boolean makeSureFolderExists() {
        if (this.serversWrapperFolder == null)
            this.serversWrapperFolder = new File(client.gameDirectory, "s760");
        return serversWrapperFolder.exists() || serversWrapperFolder.mkdirs();
    }

    @Override
    public void load() {
        try {
            getInternalServers().clear();
            getInternalHiddenServers().clear();

            if (makeSureFolderExists()) {
                CompoundTag compoundTag = NbtIo.read(new File(serversWrapperFolder, "servers" + pageIndex + ".dat").toPath());
                if (compoundTag == null)
                    return;

                if (compoundTag.contains("name", Tag.TAG_STRING))
                    listName = compoundTag.getString("name");
                ListTag listTag = compoundTag.getList("servers", Tag.TAG_COMPOUND);

                for (int i = 0; i < listTag.size(); ++i) {
                    CompoundTag c = listTag.getCompound(i);
                    ServerData serverInfo = ServerData.read(c);
                    if (c.getBoolean("hidden")) getInternalHiddenServers().add(serverInfo);
                    else getInternalServers().add(serverInfo);
                }
            }
        } catch (Exception var4) {
            LOGGER.error("Couldn't load server list", var4);
        }
    }

    @Override
    public void save() {
        try {
            if (makeSureFolderExists()) {
                ListTag listTag = new ListTag();
                for (ServerData serverInfo : getInternalServers()) {
                    CompoundTag c = serverInfo.write();
                    c.putBoolean("hidden", false);
                    listTag.add(c);
                }
                for (ServerData serverInfo : getInternalHiddenServers()) {
                    CompoundTag c = serverInfo.write();
                    c.putBoolean("hidden", true);
                    listTag.add(c);
                }

                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("name", listName);
                compoundTag.put("servers", listTag);

                String n = "servers" + pageIndex;
                File tempFile = File.createTempFile(n, ".dat", this.serversWrapperFolder);
                NbtIo.write(compoundTag, tempFile.toPath());
                File serversDatOld = new File(this.serversWrapperFolder, n + ".dat_old");
                File serversDat = new File(this.serversWrapperFolder, n + ".dat");
                Util.safeReplaceFile(serversDat.toPath() , tempFile.toPath(), serversDatOld.toPath());
            }
        } catch (Exception var6) {
            LOGGER.error("Couldn't save server list", var6);
        }
    }

    public void deleteFile() {
        try {
            if (makeSureFolderExists()) {
                File file3 = new File(this.serversWrapperFolder, "servers" + pageIndex + ".dat");
                file3.delete();
            }
        } catch (Exception var6) {
            LOGGER.error("Couldn't remove the server list", var6);
        }
    }

    public void setName(String value) {
        listName = value;
    }

    public String getName() {
        return listName;
    }
}
