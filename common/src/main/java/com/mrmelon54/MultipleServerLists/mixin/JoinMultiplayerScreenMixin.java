package com.mrmelon54.MultipleServerLists.mixin;

import com.mrmelon54.MultipleServerLists.MultipleServerLists;
import com.mrmelon54.MultipleServerLists.client.gui.components.TabViewWidget;
import com.mrmelon54.MultipleServerLists.client.screen.EditListNameScreen;
import com.mrmelon54.MultipleServerLists.duck.EntryListWidgetDuckProvider;
import com.mrmelon54.MultipleServerLists.duck.MultiplayerScreenDuckProvider;
import com.mrmelon54.MultipleServerLists.util.CustomFileServerList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public class JoinMultiplayerScreenMixin extends Screen implements MultiplayerScreenDuckProvider {
    @Shadow
    protected ServerSelectionList serverSelectionList;
    @Shadow
    private ServerList servers;
    @Shadow
    @Final
    private Screen lastScreen;
    @Unique
    private int multiple_server_lists$currentTab = 0;
    @Unique
    private Button multiple_server_lists$editServerListNameButton;
    @Unique
    private ItemStack multiple_server_lists$featherStack;
    @Unique
    private TabViewWidget multiple_server_lists$tabsWidget;

    protected JoinMultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void injectedInit(CallbackInfo ci) {
        MultipleServerLists.setMultiplayerScreen(this);
        multiple_server_lists$featherStack = new ItemStack(Items.FEATHER);

        if (this.serverSelectionList instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
            entryListWidgetDuckProvider.multiple_server_lists$setRefreshCallback(() -> {
                multiple_server_lists$reloadServerList();
                if (multiple_server_lists$tabsWidget != null)
                    multiple_server_lists$tabsWidget.reloadTabList();
            });

        if (MultipleServerLists.config.showTabs)
            multiple_server_lists$tabsWidget = addRenderableWidget(new TabViewWidget(minecraft, this, width, 32));
        else {
            addRenderableWidget(Button.builder(Component.literal("<"), (button) -> {
                multiple_server_lists$currentTab--;
                if (serverSelectionList instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
                    entryListWidgetDuckProvider.multiple_server_lists$resetScrollPosition();
                multiple_server_lists$reloadServerList();
            }).bounds(0, 0, 20, 20).build());
            addRenderableWidget(Button.builder(Component.literal(">"), (button) -> {
                multiple_server_lists$currentTab++;
                if (serverSelectionList instanceof EntryListWidgetDuckProvider entryListWidgetDuckProvider)
                    entryListWidgetDuckProvider.multiple_server_lists$resetScrollPosition();
                multiple_server_lists$reloadServerList();
            }).bounds(20, 0, 20, 20).build());
            multiple_server_lists$editServerListNameButton = addRenderableWidget(Button.builder(Component.literal(""), (button) -> {
                if (servers instanceof CustomFileServerList customFileServerList) {
                    EditListNameScreen editListNameScreen = new EditListNameScreen(Component.translatable("multiple-server-lists.screen.edit-list-name.title"), this, customFileServerList);
                    if (this.minecraft != null)
                        this.minecraft.setScreen(editListNameScreen);
                }
            }).bounds(40, 0, 20, 20).build());
        }
        multiple_server_lists$reloadServerList();
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 32))
    private int changeServerListTop(int constant) {
        return 52;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void injectedRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.minecraft == null) return;
        if (MultipleServerLists.config.showTabs) return;
        if (multiple_server_lists$featherStack != null)
            guiGraphics.renderFakeItem(multiple_server_lists$featherStack, 42, 1);

        if (multiple_server_lists$currentTab == 0) guiGraphics.drawString(minecraft.font, Component.literal("Main"), 64, 6, 0xffffff);
        else {
            if (servers instanceof CustomFileServerList customFileServerList)
                guiGraphics.drawString(minecraft.font, Component.literal(customFileServerList.getName()), 64, 6, 0xffffff);
            else
                guiGraphics.drawString(minecraft.font, Component.literal("Page " + multiple_server_lists$currentTab), 64, 6, 0xffffff);
        }
    }

    @Unique
    private void multiple_server_lists$reloadServerList() {
        // Stop underflow
        if (multiple_server_lists$currentTab < 0) multiple_server_lists$currentTab = 0;

        servers = multiple_server_lists$getServerListForTab(multiple_server_lists$currentTab);
        if (servers == null) return;
        servers.load();

        if (serverSelectionList != null) this.serverSelectionList.updateOnlineServers(servers);

        if (this.multiple_server_lists$editServerListNameButton != null)
            this.multiple_server_lists$editServerListNameButton.active = servers instanceof CustomFileServerList;
    }

    @Override
    public ServerList multiple_server_lists$getServerListForTab(int tab) {
        if (tab < 0) return null;
        return tab == 0 ? new ServerList(minecraft) : new CustomFileServerList(minecraft, tab);
    }

    @Override
    public int multiple_server_lists$getCurrentTab() {
        return multiple_server_lists$currentTab;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "refreshServerList", at = @At("HEAD"), cancellable = true)
    private void refresh(CallbackInfo ci) {
        JoinMultiplayerScreen multiplayerScreen = new JoinMultiplayerScreen(this.lastScreen);
        if (multiplayerScreen instanceof MultiplayerScreenDuckProvider duckProvider)
            duckProvider.multiple_server_lists$setCurrentTab(multiple_server_lists$currentTab);
        minecraft.setScreen(multiplayerScreen);
        ci.cancel();
    }

    @Override
    public void multiple_server_lists$setCurrentTab(int currentTab) {
        this.multiple_server_lists$currentTab = currentTab;
        multiple_server_lists$reloadServerList();
    }
}
