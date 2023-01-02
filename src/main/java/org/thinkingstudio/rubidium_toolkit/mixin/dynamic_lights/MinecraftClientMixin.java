package org.thinkingstudio.rubidium_toolkit.mixin.dynamic_lights;

import org.thinkingstudio.rubidium_toolkit.features.dynamic_lights.RubidiumDynLights;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to MinecraftClient.
 * <p>
 * Goal: clear light sources cache when changing world.
 *
 * @author LambdAurora
 * @version 1.3.2
 * @since 1.3.2
 */
@Mixin(Minecraft.class)
public class MinecraftClientMixin {
	@Inject(method = "setLevel", at = @At("HEAD"))
	private void onSetWorld(ClientLevel world, CallbackInfo ci) {
		RubidiumDynLights.get().clearLightSources();
	}
}
