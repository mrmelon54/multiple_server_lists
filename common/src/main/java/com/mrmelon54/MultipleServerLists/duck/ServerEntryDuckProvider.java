package com.mrmelon54.MultipleServerLists.duck;

import net.minecraft.client.gui.GuiGraphics;

public interface ServerEntryDuckProvider {
    void multiple_server_lists$extendedRender(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, boolean isScrollable);
}
