package de.cravingsex.skinviewer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SkinViewerConfigScreen extends Screen {
    private final Screen parent;
    private Button menuNameButton;
    private Button ingameNameButton;
    private Button positionButton;

    public SkinViewerConfigScreen(Screen parent) {
        super(Component.literal("SkinViewer"));
        this.parent = parent;
    }

    @Override protected void init() {
        menuNameButton = addRenderableWidget(Button.builder(menuNameText(), button -> { SkinViewerConfig.toggleMenuName(); button.setMessage(menuNameText()); }).bounds(width / 2 - 110, height / 2 - 38, 220, 20).build());
        ingameNameButton = addRenderableWidget(Button.builder(ingameNameText(), button -> { SkinViewerConfig.toggleIngameNameTag(); button.setMessage(ingameNameText()); }).bounds(width / 2 - 110, height / 2 - 12, 220, 20).build());
        positionButton = addRenderableWidget(Button.builder(positionText(), button -> { SkinViewerConfig.togglePosition(); button.setMessage(positionText()); }).bounds(width / 2 - 110, height / 2 + 14, 220, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose()).bounds(width / 2 - 110, height / 2 + 50, 220, 20).build());
    }

    private Component menuNameText() { return Component.literal("Show Name in Menu: " + (SkinViewerConfig.showMenuName() ? "ON" : "OFF")); }
    private Component ingameNameText() { return Component.literal("Show NameTag Ingame: " + (SkinViewerConfig.showIngameNameTag() ? "ON" : "OFF")); }
    private Component positionText() { return Component.literal("Position: " + SkinViewerConfig.position()); }
    @Override public void onClose() { minecraft.gui.setScreen(parent); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(width / 2 - 125, height / 2 - 70, width / 2 + 125, height / 2 + 86, 0xB0000000);
        graphics.centeredText(font, title, width / 2, height / 2 - 74, 0xFFFFFF);
        graphics.centeredText(font, Component.literal("Drag the menu model with the left mouse button"), width / 2, height / 2 - 62, 0xA0A0A0);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
}
