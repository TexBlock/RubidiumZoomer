package com.texstudio.rubidium_toolkit.mixin.DynLights.lightsource;

import com.texstudio.rubidium_toolkit.features.DynLights.DynamicLightSource;
import com.texstudio.rubidium_toolkit.features.DynLights.RubidiumDynLights;
import com.texstudio.rubidium_toolkit.features.DynLights.api.DynamicLightHandlers;
import com.texstudio.rubidium_toolkit.features.DynLights.config.DynamicLightsConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class TntEntityMixin extends Entity implements DynamicLightSource {
	@Shadow
	public abstract int getFuse();

	@Unique
	private int startFuseTimer = 80;
	@Unique
	private int lambdynlights$luminance;

	public TntEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
	private void onNew(EntityType<? extends PrimedTnt> entityType, Level world, CallbackInfo ci) {
		this.startFuseTimer = this.getFuse();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		// We do not want to update the entity on the server.
		if (this.getCommandSenderWorld().isClientSide()) {
			if (!RubidiumDynLights.isEnabled())
				return;

			if (this.isRemoved()) {
				this.setDynamicLightEnabled(false);
			} else {
				if (!DynamicLightsConfig.TileEntityLighting.get() || !DynamicLightHandlers.canLightUp(this))
					this.resetDynamicLight();
				else
					this.dynamicLightTick();
				RubidiumDynLights.updateTracking(this);
			}
		}
	}

	@Override
	public void dynamicLightTick() {
		if (this.isOnFire()) {
			this.lambdynlights$luminance = 15;
		} else {
			if (RubidiumDynLights.isEnabled()) {
				var fuse = this.getFuse() / this.startFuseTimer;
				this.lambdynlights$luminance = (int) (-(fuse * fuse) * 10.0) + 10;
			} else {
				this.lambdynlights$luminance = 10;
			}
		}
	}

	@Override
	public int getLuminance() {
		return this.lambdynlights$luminance;
	}
}
