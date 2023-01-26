package org.thinkingstudio.rubidium_toolkit.mixin.dynlights.lightsource;

import org.thinkingstudio.rubidium_toolkit.features.dynlights.DynamicLightSource;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.ToolkitDynLights;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.api.DynamicLightHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements DynamicLightSource {
	@Shadow
	public abstract boolean isSpectator();

	@Unique
	protected int lambdynlights$luminance;
	@Unique
	private Level lambdynlights$lastWorld;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Override
	public void dynamicLightTick() {
		if (!DynamicLightHandlers.canLightUp(this)) {
			this.lambdynlights$luminance = 0;
			return;
		}

		if (this.isOnFire() || this.isCurrentlyGlowing()) {
			this.lambdynlights$luminance = 15;
		} else {
			int luminance = DynamicLightHandlers.getLuminanceFrom((Entity) this);

			var eyePos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
			boolean submergedInFluid = !this.level.getFluidState(eyePos).isEmpty();
			for (var equipped : this.getAllSlots()) {
				if (!equipped.isEmpty())
					luminance = Math.max(luminance, ToolkitDynLights.getLuminanceFromItemStack(equipped, submergedInFluid));
			}

			this.lambdynlights$luminance = luminance;
		}

		if (this.isSpectator())
			this.lambdynlights$luminance = 0;

		if (this.lambdynlights$lastWorld != this.getCommandSenderWorld()) {
			this.lambdynlights$lastWorld = this.getCommandSenderWorld();
			this.lambdynlights$luminance = 0;
		}
	}

	@Override
	public int getLuminance() {
		return this.lambdynlights$luminance;
	}
}
