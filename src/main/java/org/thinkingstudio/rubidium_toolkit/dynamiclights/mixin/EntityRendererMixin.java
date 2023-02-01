package org.thinkingstudio.rubidium_toolkit.dynamiclights.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.DynamicLightSource;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.DynamicLightsFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
    private void onGetBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!DynamicLightsFeature.isEnabled())
            return; // Do not touch to the value.

        int vanilla = cir.getReturnValueI();
        int entityLuminance = ((DynamicLightSource) entity).getLuminance();
        if (entityLuminance >= 15)
            cir.setReturnValue(entityLuminance);

        int posLuminance = (int) DynamicLightsFeature.getDynamicLightLevel(pos);

        cir.setReturnValue(Math.max(Math.max(vanilla, entityLuminance), posLuminance));
    }
}
