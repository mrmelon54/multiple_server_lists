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
    private static final ResourceLocation SERVER_SELECTION_TEXTURE = new ResourceLocation("textures/gui/server_selection.png");

    @Unique
    @Override
    public void multiple_server_lists$extendedRender(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable) {
        // prevent the LANHeader and LAN server entry from having move left and right buttons
        //noinspection ConstantValue
        if (!((Object) this instanceof ServerSelectionList.OnlineServerEntry)) return;

        RenderSystem.setShaderTexture(0, SERVER_SELECTION_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int t = x + entryWidth;
        int u = isScrollable ? t + 6 : t;
        int v = mouseX - x;
        if (hovered) {
            // Move server left arrow
            if (v < 0 && v > -16)
                guiGraphics.blit(SERVER_SELECTION_TEXTURE, x - 16, y, 16, 32, 32, 32, 16, 32, 256, 256);
            else
                guiGraphics.blit(SERVER_SELECTION_TEXTURE, x - 16, y, 16, 32, 32, 0, 16, 32, 256, 256);

            // Move server right arrow
            if (v - entryWidth > 0 && v - entryWidth < 16)
                guiGraphics.blit(SERVER_SELECTION_TEXTURE, u, y, 16, 32, 16, 32, 16, 32, 256, 256);
            else
                guiGraphics.blit(SERVER_SELECTION_TEXTURE, u, y, 16, 32, 16, 0, 16, 32, 256, 256);
        }
    }
}
