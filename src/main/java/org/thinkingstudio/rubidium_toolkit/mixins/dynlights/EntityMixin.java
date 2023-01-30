package org.thinkingstudio.rubidium_toolkit.mixins.dynlights;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.DynamicLightSource;
import org.thinkingstudio.rubidium_toolkit.config.ToolkitConfig;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.DynamicLightsFeature;
import org.thinkingstudio.rubidium_toolkit.features.dynlights.api.DynamicLightHandlers;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Entity.class)
public abstract class EntityMixin implements DynamicLightSource {
    @Shadow
    public Level level;

    @Shadow
    public boolean removed;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract double getY();

    @Shadow
    public int xChunk;
    @Shadow
    public int zChunk;

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract BlockPos blockPosition();

    private int lambdynlights_luminance = 0;
    private int lambdynlights_lastLuminance = 0;
    private long lambdynlights_lastUpdate = 0;
    private double lambdynlights_prevX;
    private double lambdynlights_prevY;
    private double lambdynlights_prevZ;
    private LongOpenHashSet trackedLitChunkPos = new LongOpenHashSet();

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        // We do not want to update the entity on the server.
        if (this.level.isClientSide) {
            if (this.removed) {
                this.setDynamicLightEnabled(false);
            } else {
                this.dynamicLightTick();
                if (!ToolkitConfig.entityLighting.get() && this.getType() != EntityType.PLAYER)
                    this.lambdynlights_luminance = 0;
                DynamicLightsFeature.updateTracking(this);
            }
        }
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void onRemove(CallbackInfo ci) {
        if (this.level.isClientSide())
            this.setDynamicLightEnabled(false);
    }

    @Override
    public double getDynamicLightX() {
        return this.getX();
    }

    @Override
    public double getDynamicLightY() {
        return this.getEyeY();
    }

    @Override
    public double getDynamicLightZ() {
        return this.getZ();
    }

    @Override
    public Level getDynamicLightWorld() {
        return this.level;
    }

    @Override
    public void resetDynamicLight() {
        this.lambdynlights_lastLuminance = 0;
    }

    @Override
    public boolean shouldUpdateDynamicLight() {
        return DynamicLightsFeature.ShouldUpdateDynamicLights();
    }

    @Override
    public void dynamicLightTick() {
        this.lambdynlights_luminance = this.isOnFire() ? 15 : 0;

        int luminance = DynamicLightHandlers.getLuminanceFrom((Entity) (Object) this);
        if (luminance > this.lambdynlights_luminance)
            this.lambdynlights_luminance = luminance;
    }

    @Override
    public int getLuminance() {
        return this.lambdynlights_luminance;
    }

    @Override
    public boolean lambdynlights_updateDynamicLight(@NotNull LevelRenderer renderer) {
        if (!this.shouldUpdateDynamicLight())
            return false;
        double deltaX = this.getX() - this.lambdynlights_prevX;
        double deltaY = this.getY() - this.lambdynlights_prevY;
        double deltaZ = this.getZ() - this.lambdynlights_prevZ;

        int luminance = this.getLuminance();


        double minDelta = 0D;
        String mode = ToolkitConfig.quality.get();
        if (Objects.equals(mode, "SLOW"))
            minDelta = 0.5D;

        if (Objects.equals(mode, "FAST") )
            minDelta = 0.20D;


        if (Math.abs(deltaX) > minDelta || Math.abs(deltaY) > minDelta || Math.abs(deltaZ) > minDelta || luminance != this.lambdynlights_lastLuminance) {
            this.lambdynlights_prevX = this.getX();
            this.lambdynlights_prevY = this.getY();
            this.lambdynlights_prevZ = this.getZ();
            this.lambdynlights_lastLuminance = luminance;

            LongOpenHashSet newPos = new LongOpenHashSet();

            if (luminance > 0) {
                BlockPos.MutableBlockPos chunkPos = new BlockPos.MutableBlockPos(this.xChunk, Mth.getInt(String.valueOf((int) this.getEyeY()), 16), this.zChunk);

                DynamicLightsFeature.scheduleChunkRebuild(renderer, chunkPos);
                DynamicLightsFeature.updateTrackedChunks(chunkPos, this.trackedLitChunkPos, newPos);

                Direction directionX = (this.blockPosition().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
                Direction directionY = (Mth.fastFloor(this.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                Direction directionZ = (this.blockPosition().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX); // X
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ); // XZ
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite()); // Z
                    } else {
                        chunkPos.move(directionZ.getOpposite()); // origin
                        chunkPos.move(directionY); // Y
                    }
                    DynamicLightsFeature.scheduleChunkRebuild(renderer, chunkPos);
                    DynamicLightsFeature.updateTrackedChunks(chunkPos, this.trackedLitChunkPos, newPos);
                }
            }

            // Schedules the rebuild of removed chunks.
            this.lambdynlights_scheduleTrackedChunksRebuild(renderer);
            // Update tracked lit chunks.
            this.trackedLitChunkPos = newPos;
            return true;
        }
        return false;
    }

    @Override
    public void lambdynlights_scheduleTrackedChunksRebuild(@NotNull LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.level)
            for (long pos : this.trackedLitChunkPos) {
                DynamicLightsFeature.scheduleChunkRebuild(renderer, pos);
            }
    }
}
