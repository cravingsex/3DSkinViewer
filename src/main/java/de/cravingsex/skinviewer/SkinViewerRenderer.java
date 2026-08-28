package de.cravingsex.skinviewer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Supplier;

/** Renders the game's own live LocalPlayer model, including all enabled layers. */
public final class SkinViewerRenderer {
    private static final int SIDE_MARGIN = 250;
    // The vanilla name tag sits well above the 72px avatar. Keep a generous
    // off-screen render area so the title-screen tag is never texture-clipped.
    private static final int VIEWER_HALF_WIDTH = 100;
    private static final int VIEWER_TOP_PADDING = 175;
    private static final int VIEWER_BOTTOM_PADDING = 303;
    private static Supplier<PlayerSkin> titleSkinLookup;
    private static boolean dragging;
    private static float yaw = 20.0f;
    private static float pitch = 8.0f;
    private SkinViewerRenderer() {}

    public static void render(GuiGraphicsExtractor graphics, int width, int height, float partialTick, boolean pauseMenu) {
        LocalPlayer player = Minecraft.getInstance().player;
        int sideX = SkinViewerConfig.position() == SkinViewerConfig.Position.LEFT ? Math.min(SIDE_MARGIN, width / 2 - 72) : Math.max(width - SIDE_MARGIN, width / 2 + 72);
        int top = viewerTop(height, pauseMenu);
        if (player != null) {
            renderPlayerAvatar(graphics, player, sideX, top, partialTick);
        } else {
            renderTitleAvatar(graphics, sideX, top);
        }
        renderFixedMenuName(graphics, player, sideX, top);
    }

    public static boolean beginDrag(int mouseX, int mouseY, int width, int height, boolean pauseMenu) {
        int x = SkinViewerConfig.position() == SkinViewerConfig.Position.LEFT ? Math.min(SIDE_MARGIN, width / 2 - 72) : Math.max(width - SIDE_MARGIN, width / 2 + 72);
        int y = viewerTop(height, pauseMenu);
        dragging = mouseX >= x - 70 && mouseX <= x + 70 && mouseY >= y - 55 && mouseY <= y + 183;
        return dragging;
    }

    public static void drag(double deltaX, double deltaY) {
        if (!dragging) return;
        yaw += (float) deltaX * 1.2f;
        if (yaw > 180.0f) yaw -= 360.0f;
        if (yaw < -180.0f) yaw += 360.0f;
        pitch += (float) deltaY * 0.7f;
        if (pitch > 180.0f) pitch -= 360.0f;
        if (pitch < -180.0f) pitch += 360.0f;
    }

    public static void endDrag() { dragging = false; }

    private static int viewerTop(int height, boolean pauseMenu) {
        int base = pauseMenu ? height / 2 - 135 : height / 2 - 65;
        return Math.max(38, base + SkinViewerConfig.verticalOffset());
    }

    private static void renderTitleAvatar(GuiGraphicsExtractor graphics, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        if (titleSkinLookup == null) titleSkinLookup = minecraft.getSkinManager().createLookup(minecraft.getGameProfile(), true);
        AvatarRenderState state = new AvatarRenderState();
        state.skin = titleSkinLookup.get();
        state.boundingBoxWidth = 0.6f;
        state.boundingBoxHeight = 1.8f;
        state.scale = 1.0f;
        state.ageScale = 1.0f;
        state.pose = Pose.STANDING;
        state.showHat = state.showJacket = state.showLeftPants = state.showRightPants = true;
        state.showLeftSleeve = state.showRightSleeve = true;
        state.showCape = true;
        state.nameTag = null;
        applyViewerRotation(state);
        Quaternionf body = modelRotation();
        Quaternionf camera = cameraRotation();
        graphics.entity(state, SkinViewerConfig.viewerScale(), new Vector3f(0.0f, 0.9f, 0.0f), body, camera,
                x - VIEWER_HALF_WIDTH, y - VIEWER_TOP_PADDING, x + VIEWER_HALF_WIDTH, y + VIEWER_BOTTOM_PADDING);
    }

    private static void renderPlayerAvatar(GuiGraphicsExtractor graphics, LocalPlayer player, int x, int y, float partialTick) {
        AvatarRenderState state = staticPlayerState(player);
        state.nameTag = null;
        applyViewerRotation(state);
        graphics.entity(state, SkinViewerConfig.viewerScale(), new Vector3f(0.0f, 0.9f, 0.0f), modelRotation(), cameraRotation(),
                x - VIEWER_HALF_WIDTH, y - VIEWER_TOP_PADDING, x + VIEWER_HALF_WIDTH, y + VIEWER_BOTTOM_PADDING);
    }

    private static void applyViewerRotation(AvatarRenderState state) {
        state.bodyRot = 180.0f;
        state.yRot = 0.0f;
        state.xRot = 0.0f;
    }

    /** Model stays upright; horizontal drag turns it on the vertical axis only. */
    private static Quaternionf modelRotation() {
        return new Quaternionf().rotateZ((float) Math.PI)
                .rotateY((float) Math.toRadians(yaw));
    }

    /** Vertical drag changes the observer angle, never the mannequin pose. */
    private static Quaternionf cameraRotation() {
        return new Quaternionf().rotateX((float) Math.toRadians(pitch));
    }

    /** Builds a neutral mannequin state; no walk, arm, cape, or combat animation is copied. */
    private static AvatarRenderState staticPlayerState(LocalPlayer player) {
        AvatarRenderState state = new AvatarRenderState();
        state.skin = player.getSkin();
        state.boundingBoxWidth = 0.6f;
        state.boundingBoxHeight = 1.8f;
        state.eyeHeight = 1.62f;
        state.scale = 1.0f;
        state.ageScale = 1.0f;
        state.pose = Pose.STANDING;
        state.showHat = player.isModelPartShown(PlayerModelPart.HAT);
        state.showJacket = player.isModelPartShown(PlayerModelPart.JACKET);
        state.showLeftPants = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        state.showRightPants = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        state.showLeftSleeve = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        state.showRightSleeve = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        state.showCape = player.isModelPartShown(PlayerModelPart.CAPE);
        return state;
    }

    /** A fixed GUI-space copy of the vanilla name-tag style. */
    private static void renderFixedMenuName(GuiGraphicsExtractor graphics, LocalPlayer player, int x, int y) {
        if (!SkinViewerConfig.showMenuName()) return;
        var name = player != null ? player.getName() : net.minecraft.network.chat.Component.literal(Minecraft.getInstance().getUser().getName());
        int textWidth = Minecraft.getInstance().font.width(name);
        // Entity PIP is processed in the current stratum. Move text into the
        // following one so it is drawn above the transparent PIP texture.
        graphics.nextStratum();
        graphics.textWithBackdrop(Minecraft.getInstance().font, name, x - textWidth / 2, Math.max(6, y - 82), 0xFFFFFFFF, 0x40000000);
    }

}
