package org.thinkingstudio.rubidium_toolkit.mixin.fog;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SodiumWorldRenderer.class)
public class MixinSodiumWorldRenderer {

    /**
    * Sodium's 1.16.x/dev fog, added because removed from 1.16.x/next.
    * @author jellysquid3

    @Inject(method = "drawChunkLayer", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/passes/BlockRenderPass;startDrawing()V", shift = At.Shift.AFTER), remap = false)
    private void drawChunkLayer(RenderType renderLayer, PoseStack matrixStack, double x, double y, double z, CallbackInfo info) {
        // We don't have a great way to check if underwater fog is being used, so assume that terrain will only ever
        // use linear fog. This will not disable fog in the Nether.
        if (!MagnesiumExtrasConfig.fog.get() && this.isFogLinear()) {
            RenderSystem.setShaderFogEnd();
        }
    }

    private boolean isFogLinear() {
        return GL11.glGetInteger(GL11.GL_FOG_MODE) == GL11.GL_LINEAR;
    }
     */
}
