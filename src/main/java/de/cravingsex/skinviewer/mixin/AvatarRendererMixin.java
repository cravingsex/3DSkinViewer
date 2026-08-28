package de.cravingsex.skinviewer.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import de.cravingsex.skinviewer.SkinViewerConfig;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityAttachment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Adds precisely one vanilla-style label to the local avatar while in third person. */
@Mixin(AvatarRenderer.class)
abstract class AvatarRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void skinviewer$addLocalThirdPersonName(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!SkinViewerConfig.showIngameNameTag() || avatar != minecraft.player || minecraft.options.getCameraType() == CameraType.FIRST_PERSON) return;

        state.nameTag = avatar.getName();
        state.nameTagAttachment = avatar.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, avatar.getYRot(partialTick));
    }
}
