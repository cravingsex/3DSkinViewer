package de.cravingsex.skinviewer.mixin;

import de.cravingsex.skinviewer.SkinViewerRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
abstract class ScreenMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void skinviewer$render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        if (self instanceof TitleScreen || self instanceof PauseScreen) {
            SkinViewerRenderer.render(graphics, self.width, self.height, partialTick, self instanceof PauseScreen);
        }
    }

}
