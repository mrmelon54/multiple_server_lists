package com.mrmelon54.MultipleServerLists.mixin;

import com.mrmelon54.MultipleServerLists.duck.EntryListWidgetDuckProvider;
import com.mrmelon54.MultipleServerLists.duck.MultiplayerScreenDuckProvider;
import com.mrmelon54.MultipleServerLists.duck.ServerEntryDuckProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E> extends AbstractContainerWidget implements EntryListWidgetDuckProvider {
    public AbstractSelectionListMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Shadow
    public abstract int getMaxScroll();

    @Shadow
    protected abstract int getItemCount();

    @Shadow
    protected abstract int getRowTop(int i);

    @Shadow
    protected abstract int getRowBottom(int i);

    @Shadow
    @Final
    protected int itemHeight;
    @Shadow
    @Final
    private List<E> children;

    @Shadow
    public abstract int getRowWidth();

    @Shadow
    public abstract int getRowLeft();

    @Shadow
    @Final
    protected Minecraft minecraft;
    @Shadow
    protected int headerHeight;

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    private double scrollAmount;

    @Unique
    private Runnable multiple_server_lists$refreshCallback;

    @Inject(method = "renderList", at = @At("TAIL"))
    private void injectedRenderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //noinspection ConstantConditions
        if (!(((Object) this) instanceof ServerSelectionList)) return;

        E pEntry = this.multiple_server_lists$getEntryAtOffsetY(mouseY);
        boolean isScrollable = this.getMaxScroll() > 0;
        int i = this.getItemCount();
        for (int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l < getY() || k > getBottom()) continue;

            int n = this.itemHeight - 4;
            E entry = this.children.get(j);
            int o = this.getRowWidth();
            int r = this.getRowLeft();
            if (entry instanceof ServerEntryDuckProvider serverEntryDuckProvider)
                serverEntryDuckProvider.multiple_server_lists$extendedRender(guiGraphics, j, k, r, o, n, mouseX, mouseY, Objects.equals(pEntry, entry), delta, isScrollable);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void injectedMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantConditions
        if (!(((Object) this) instanceof ServerSelectionList)) return;
        int pIdx = this.multiple_server_lists$getEntryIndex(mouseY);
        E pEntry = this.multiple_server_lists$getEntryAtOffsetY(mouseY);
        if (pEntry == null) return;

        if (!(this.minecraft.screen instanceof MultiplayerScreenDuckProvider duckProvider)) return;

        boolean isScrollable = this.getMaxScroll() > 0;
        int t = this.getRowLeft() + this.getRowWidth() + (isScrollable ? 6 : 0);
        int u = this.getRowLeft() - 6;
        int q = 0;
        if (mouseX < u && mouseX >= u - 14) q = -1;
        else if (mouseX >= t && mouseX < t + 14) q = 1;

        if (q == 0) return;
        ServerList firstServerList = duckProvider.multiple_server_lists$getServerListForTab(duckProvider.multiple_server_lists$getCurrentTab());
        if (pIdx < 0 || pIdx > firstServerList.size()) return; // has invalid server

        ServerList secondServerList = duckProvider.multiple_server_lists$getServerListForTab(duckProvider.multiple_server_lists$getCurrentTab() + q);
        if (firstServerList != null && secondServerList != null) {
            // Load
            firstServerList.load();
            secondServerList.load();

            // Grab and move
            ServerData serverData = firstServerList.get(pIdx);
            firstServerList.remove(serverData);
            secondServerList.add(serverData, false);

            // Save
            firstServerList.save();
            secondServerList.save();
        }

        multiple_server_lists$resetScrollPosition();
        if (multiple_server_lists$refreshCallback != null) multiple_server_lists$refreshCallback.run();

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Unique
    private int multiple_server_lists$getEntryIndex(double y) {
        int m = Mth.floor(y - (double) getY()) - headerHeight + (int) getScrollAmount() - 4;
        return m / this.itemHeight;
    }

    @Unique
    @Nullable
    private E multiple_server_lists$getEntryAtOffsetY(double y) {
        int m = Mth.floor(y - (double) getY()) - headerHeight - (int) getScrollAmount() - 4;
        int n = m / this.itemHeight;
        return n >= 0 && m >= 0 && n < this.getItemCount() ? this.children.get(n) : null;
    }

    @Override
    public void multiple_server_lists$resetScrollPosition() {
        scrollAmount = 0;
    }

    @Override
    public void multiple_server_lists$setRefreshCallback(Runnable callback) {
        multiple_server_lists$refreshCallback = callback;
    }
}
