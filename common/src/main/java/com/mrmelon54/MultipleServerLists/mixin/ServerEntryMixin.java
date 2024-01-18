package com.mrmelon54.MultipleServerLists.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrmelon54.MultipleServerLists.duck.ServerEntryDuckProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerSelectionList.Entry.class)
public abstract class ServerEntryMixin extends ObjectSelectionList.Entry<ServerSelectionList.Entry> implements ServerEntryDuckProvider {
    @Unique
    private static final ResourceLocation MOVE_LEFT_ARROW = new ResourceLocation("multiple-server-lists", "move_left");
    @Unique
    private static final ResourceLocation MOVE_LEFT_ARROW_HIGHLIGHT = new ResourceLocation("multiple-server-lists", "move_left_highlight");
    @Unique
    private static final ResourceLocation MOVE_RIGHT_ARROW = new ResourceLocation("multiple-server-lists", "move_right");
    @Unique
    private static final ResourceLocation MOVE_RIGHT_ARROW_HIGHLIGHT = new ResourceLocation("multiple-server-lists", "move_right_highlight");

    @Unique
    @Override
    public void multiple_server_lists$extendedRender(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable) {
        // prevent the LANHeader and LAN server entry from having move left and right buttons
        //noinspection ConstantValue
        if (!((Object) this instanceof ServerSelectionList.OnlineServerEntry)) return;

        int t = x + entryWidth + (isScrollable ? 6 : 0);
        int u = x - 6;
        if (!hovered) return;

        // Move server left arrow
        if (mouseX < u && mouseX >= u - 14) guiGraphics.blitSprite(MOVE_LEFT_ARROW_HIGHLIGHT, u - 14, y, 14, 32);
        else guiGraphics.blitSprite(MOVE_LEFT_ARROW, u - 14, y, 14, 32);

        // Move server right arrow
        if (mouseX >= t && mouseX < t + 14) guiGraphics.blitSprite(MOVE_RIGHT_ARROW_HIGHLIGHT, t, y, 14, 32);
        else guiGraphics.blitSprite(MOVE_RIGHT_ARROW, t, y, 14, 32);
    }
}
