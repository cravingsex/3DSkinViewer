package de.cravingsex.skinviewer.mixin;

import de.cravingsex.skinviewer.SkinViewerRenderer;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hooks the 26.2 input interface so drag rotation works without replacing screen controls. */
@Mixin(ContainerEventHandler.class)
interface ContainerEventHandlerMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void skinviewer$beginSkinDrag(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (self instanceof Screen screen && (screen instanceof TitleScreen || screen instanceof PauseScreen)
                && event.button() == 0 && SkinViewerRenderer.beginDrag((int) event.x(), (int) event.y(), screen.width, screen.height, screen instanceof PauseScreen)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"))
    private void skinviewer$dragSkin(MouseButtonEvent event, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Screen) SkinViewerRenderer.drag(deltaX, deltaY);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void skinviewer$endSkinDrag(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == 0) SkinViewerRenderer.endDrag();
    }
}
