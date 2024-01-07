package com.mrmelon54.MultipleServerLists.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrmelon54.MultipleServerLists.MultipleServerLists;
import com.mrmelon54.MultipleServerLists.client.screen.EditListNameScreen;
import com.mrmelon54.MultipleServerLists.util.CustomFileServerList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TabViewWidget extends ObjectSelectionList<TabViewWidget.TabWidget> {
    private static final ResourceLocation SERVER_TABS_TEXTURE = new ResourceLocation("multiple-server-lists", "textures/gui/server_tabs.png");
    private final ItemStack featherStack;
    private List<CustomFileServerList> serverLists;
    private ScrollableRegion scrollRegion;
    private final Button scrollLeft;
    private final Button scrollRight;
    private final Button addTab;
    private final Button editServerListNameButton;

    public TabViewWidget(Minecraft mc, Screen screen, int width, int top) {
        super(mc, width, 20, top, top + 20, 20);
        setRenderBackground(false);
        setRenderHeader(false, 0);
        setRenderTopAndBottom(false);
        setRenderSelection(true);
        reloadTabList();
        featherStack = new ItemStack(Items.FEATHER);

        scrollToSelectedTab();

        this.scrollLeft = new ImageButton(0, top, 20, 20, 0, 80, 20, SERVER_TABS_TEXTURE, button -> scrollRegion.scrollLeft());
        this.scrollRight = new ImageButton(width - 60, top, 20, 20, 20, 80, 20, SERVER_TABS_TEXTURE, button -> scrollRegion.scrollRight());
        this.addTab = new ImageButton(width - 40, top, 20, 20, 40, 80, 20, SERVER_TABS_TEXTURE, button -> {
            List<TabWidget> children = children();
            int n = children.get(children.size() - 1).list.index();
            CustomFileServerList list = new CustomFileServerList(mc, n + 1);
            serverLists.add(list);
            list.save();
            refresh();
            for (TabWidget child : children)
                if (child.list == list) {
                    child.onPress.onPress(child);
                    return;
                }
        });
        this.editServerListNameButton = Button.builder(Component.literal(""), (button) -> {
            List<TabWidget> children = children();
            int tab = MultipleServerLists.getTab() - 1;
            System.out.println("tab:" + tab);
            if (tab < 0 || tab > children.size()) return;
            CustomFileServerList currentServerList = children.get(tab).list;
            EditListNameScreen editListNameScreen = new EditListNameScreen(Component.translatable("multiple-server-lists.screen.edit-list-name.title"), screen, currentServerList);
            if (mc != null) mc.setScreen(editListNameScreen);
        }).bounds(width - 20, top, 20, 20).build();
    }

    private void scrollToSelectedTab() {
        int selectedTab = MultipleServerLists.getTab();
        List<TabWidget> children = children();
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            TabWidget child = children.get(i);
            if (selectedTab == i) {
                child.onPress.onPress(child);
                break;
            }
        }
    }

    public void reloadTabList() {
        serverLists = MultipleServerLists.getTabServerList();
        refresh();
    }

    public void refresh() {
        List<TabWidget> c = this.children();
        c.clear();
        int totalWidth = 0;
        TabWidget t = new TabWidget(-200, 0, null, button -> {
            MultipleServerLists.setTab(0);
            scrollRegion.scrollToVisible(button.start - 20, button.start + button.getActualWidth() + 20);
        });
        totalWidth += t.getActualWidth();
        c.add(t);
        for (int i = 0; i < serverLists.size(); i++) {
            CustomFileServerList serverList = serverLists.get(i);
            final int j = i + 1;
            t = new TabWidget(-200, totalWidth, serverList, button -> {
                MultipleServerLists.setTab(j);
                scrollRegion.scrollToVisible(button.start - 20, button.start + button.getActualWidth() + 20);
            });
            totalWidth += t.getActualWidth();
            c.add(t);
        }
        scrollRegion = new ScrollableRegion(totalWidth, width);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int startX = scrollRegion.startScrollableX();
        List<TabWidget> children = children();
        int selectedTab = MultipleServerLists.getTab();
        int w = startX;
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            TabWidget child = children.get(i);
            int a = child.getActualWidth();

            // true if part of the tab is visible
            boolean tabIsVisible = w <= scrollRegion.viewWidth && w + a >= 0;
            if (tabIsVisible) {
                boolean hovered = mouseY >= y0 && mouseY < y1 && scrollRegion.isHovered(mouseX) && mouseX >= w && mouseX < w + a;
                child.selected = selectedTab == i;
                child.render(guiGraphics, i, y0, w, a, 20, mouseX, mouseY, hovered, delta);
            }
            w += a;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);
        if (scrollRegion.needsScroll()) {
            // scroll left button
            if (scrollRegion.canScrollLeft()) scrollLeft.render(guiGraphics, mouseX, mouseY, delta);
            else guiGraphics.blit(SERVER_TABS_TEXTURE, 0, y0, 0, 60, 20, 20, 256, 256);

            // scroll right button
            if (scrollRegion.canScrollRight()) scrollRight.render(guiGraphics, mouseX, mouseY, delta);
            else guiGraphics.blit(SERVER_TABS_TEXTURE, width - 60, y0, 20, 60, 20, 20, 256, 256);
        }
        addTab.render(guiGraphics, mouseX, mouseY, delta);
        if (selectedTab > 0) editServerListNameButton.render(guiGraphics, mouseX, mouseY, delta);
        else {
            guiGraphics.blit(Button.WIDGETS_LOCATION, width - 20, y0, 0, 46, 10, 20);
            guiGraphics.blit(Button.WIDGETS_LOCATION, width - 10, y0, 200 - 10, 46, 10, 20);
        }
        if (featherStack != null) guiGraphics.renderFakeItem(featherStack, width - 18, y0 + 2);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // ignore clicks outside the component
        if (mouseY < y0 || mouseY >= y1) return false;

        if (scrollRegion.needsScroll()) {
            // click left scroll
            if (this.scrollLeft.isMouseOver(mouseX, mouseY))
                return this.scrollLeft.mouseClicked(mouseX, mouseY, button);
            // click right scroll
            if (this.scrollRight.isMouseOver(mouseX, mouseY))
                return this.scrollRight.mouseClicked(mouseX, mouseY, button);
        }

        // add tab
        if (this.addTab.isMouseOver(mouseX, mouseY))
            return this.addTab.mouseClicked(mouseX, mouseY, button);
        // rename button
        if (this.editServerListNameButton.isMouseOver(mouseX, mouseY))
            return this.editServerListNameButton.mouseClicked(mouseX, mouseY, button);

        int x = (int) mouseX - scrollRegion.startScrollableX();

        // find clicked child
        List<TabWidget> children = children();
        for (TabWidget child : children)
            if (x >= child.start && x < child.start + child.getActualWidth())
                return child.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);

    }

    @Override
    public @NotNull Optional<GuiEventListener> getChildAt(double d, double e) {
        return super.getChildAt(d, e);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return super.keyPressed(i, j, k);
    }

    public static class ScrollableRegion {
        private final int totalWidth;
        private final int viewWidth;
        public int scrollX;

        public ScrollableRegion(int totalWidth, int scrollableWidth) {
            this.totalWidth = totalWidth;
            this.viewWidth = scrollableWidth;
        }

        private boolean needsScroll() {
            // 40 is the width of the rename and add buttons
            return totalWidth >= viewWidth - 40;
        }

        public int getStartX() {
            return needsScroll() ? 20 : 0;
        }

        public int getEndX() {
            // 40 for rename and add buttons
            // 60 when scroll right buttons is visible
            return viewWidth - (needsScroll() ? 60 : 40);
        }

        public int scrollableWidth() {
            return getEndX() - getStartX();
        }

        public int startScrollableX() {
            return getStartX() - scrollX;
        }

        public boolean isHovered(int mouseX) {
            return mouseX >= getStartX() && mouseX < getEndX();
        }

        public boolean canScrollLeft() {
            return scrollX > 0;
        }

        public boolean canScrollRight() {
            return scrollX < totalWidth - scrollableWidth();
        }

        public void scrollLeft() {
            scrollX = Math.max(0, scrollX - 80);
        }

        public void scrollRight() {
            scrollX = Math.min(totalWidth - scrollableWidth(), scrollX + 80);
        }

        public void scrollToVisible(int startX, int endX) {
            int scrollStart = startX + startScrollableX();
            int scrollEnd = endX + startScrollableX();
            if (scrollStart < 0) scrollX = Math.max(0, startX);
            if (scrollEnd >= scrollableWidth()) scrollX = Math.min(totalWidth - scrollableWidth(), endX - scrollableWidth());
        }
    }

    public class TabWidget extends ObjectSelectionList.Entry<TabWidget> {
        private final int width;
        private final Component message;
        private final PressAction onPress;
        public final int start;
        public boolean selected;
        public CustomFileServerList list;

        public TabWidget(int width, int start, CustomFileServerList serverList, PressAction onPress) {
            this.width = width;
            this.start = start;
            message = Component.literal(serverList == null ? "Main" : serverList.getName());
            this.onPress = onPress;
            this.list = serverList;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public @NotNull Component getNarration() {
            return message;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            onPress.onPress(this);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SERVER_TABS_TEXTURE);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            int w = getActualWidth();
            int wLeft = Mth.floorDiv(w, 2);
            int wRight = wLeft + w % 2;
            int i = selected ? 0 : hovered ? 2 : 1;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            guiGraphics.blit(SERVER_TABS_TEXTURE, x, y, 0, i * 20, wLeft, entryHeight);
            guiGraphics.blit(SERVER_TABS_TEXTURE, x + wLeft, y, 200 - wRight, i * 20, wRight, entryHeight);
            guiGraphics.drawCenteredString(minecraft.font, message, x + wLeft, y + (height - 8) / 2, 0xffffffff);
        }

        public int getActualWidth() {
            return width < 0 ? Math.max(20, minecraft.font.width(message) + 12) : width;
        }

        @Environment(EnvType.CLIENT)
        public interface PressAction {
            void onPress(TabWidget button);
        }
    }
}
