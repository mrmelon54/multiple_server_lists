package com.mrmelon54.MultipleServerLists.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrmelon54.MultipleServerLists.util.CustomFileServerList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class EditListNameScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("multiple-server-lists", "textures/gui/edit_server_name.png");
    private final Screen parent;
    private final CustomFileServerList serverList;
    private static final int backgroundWidth = 176;
    private static final int backgroundHeight = 72;
    private int x;
    private int y;
    private EditBox nameField;
    private Button renameButton;

    public EditListNameScreen(Component title, Screen parent, CustomFileServerList serverList) {
        super(title);
        this.parent = parent;
        this.serverList = serverList;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;

        if (this.serverList == null) {
            onClose();
            return;
        }

        this.renameButton = this.addRenderableWidget(Button.builder(Component.translatable("multiple-server-lists.screen.edit-list-name.button.rename"), (button) -> {
            String a = this.nameField.getValue();
            if (isValidName(a)) {
                serverList.setName(a);
                serverList.save();
                onClose();
            }
        }).bounds(this.x + 7, this.y + 45, 50, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("multiple-server-lists.screen.edit-list-name.button.cancel"), (button) -> onClose()).bounds(this.x + 119, this.y + 45, 50, 20).build());

        this.nameField = new EditBox(this.font, this.x + 62, this.y + 24, 103, 12, Component.translatable("container.repair"));
        this.nameField.setCanLoseFocus(false);
        this.nameField.setTextColor(-1);
        this.nameField.setTextColorUneditable(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(100);
        this.nameField.setResponder(this::onRenamed);
        this.nameField.setValue(this.serverList.getName());
        this.addWidget(this.nameField);
        this.setInitialFocus(this.nameField);
        this.nameField.setEditable(true);
    }

    private boolean isValidName(String a) {
        return !a.trim().isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String string = this.nameField.getValue();
        this.init(client, width, height);
        this.nameField.setValue(string);
    }

    @Override
    public void removed() {
        super.removed();
        // TODO: figure out if this is needed
        //if (this.client != null) this.client.keyboard.setRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.minecraft != null && this.minecraft.player != null)
            this.minecraft.player.closeContainer();

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -5);
        this.parent.render(guiGraphics, -2000, -2000, delta);
        guiGraphics.pose().popPose();
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xc0101010, 0xd0101010);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        guiGraphics.blit(BACKGROUND_TEXTURE, this.x, this.y, 0, 0, backgroundWidth, backgroundHeight);
        guiGraphics.blit(BACKGROUND_TEXTURE, this.x + 59, this.y + 20, 0, backgroundHeight, 110, 16);

        if (this.nameField != null) this.nameField.render(guiGraphics, mouseX, mouseY, delta);

        super.render(guiGraphics, mouseX, mouseY, delta);
    }


    private void onRenamed(String value) {
        if (this.renameButton != null) this.renameButton.active = isValidName(value);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parent != null) this.minecraft.setScreen(this.parent);
        else super.onClose();
    }
}
