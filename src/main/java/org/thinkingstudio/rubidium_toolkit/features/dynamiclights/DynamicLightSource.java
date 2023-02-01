/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package org.thinkingstudio.rubidium_toolkit.features.dynamiclights;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a dynamic light source.
 *
 * @author LambdAurora
 * @version 1.3.3
 * @since 1.0.0
 */
public interface DynamicLightSource {
    /**
     * Returns the dynamic light source X coordinate.
     *
     * @return the X coordinate
     */
    double getDynamicLightX();

    /**
     * Returns the dynamic light source Y coordinate.
     *
     * @return the Y coordinate
     */
    double getDynamicLightY();

    /**
     * Returns the dynamic light source Z coordinate.
     *
     * @return the Z coordinate
     */
    double getDynamicLightZ();

    /**
     * Returns the dynamic light source world.
     *
     * @return the world instance
     */
    World getDynamicLightWorld();

    /**
     * Returns whether the dynamic light is enabled or not.
     *
     * @return {@code true} if the dynamic light is enabled, else {@code false}
     */
    default boolean isDynamicLightEnabled()
    {
        return DynamicLightsFeatures.isEnabled() && DynamicLightsFeatures.containsLightSource(this);
    }

    /**
     * Sets whether the dynamic light is enabled or not.
     * <p>
     * Note: please do not call this function in your mod or you will break things.
     *
     * @param enabled {@code true} if the dynamic light is enabled, else {@code false}
     */
    @ApiStatus.Internal
    default void setDynamicLightEnabled(boolean enabled) {
        this.resetDynamicLight();
        if (enabled)
            DynamicLightsFeatures.addLightSource(this);
        else
            DynamicLightsFeatures.removeLightSource(this);
    }

    void resetDynamicLight();

    /**
     * Returns the luminance of the light source.
     * The maximum is 15, below 1 values are ignored.
     *
     * @return the luminance of the light source
     */
    int getLuminance();

    /**
     * Executed at each tick.
     */
    void dynamicLightTick();

    /**
     * Returns whether this dynamic light source should update.
     *
     * @return {@code true} if this dynamic light source should update, else {@code false}
     */
    boolean shouldUpdateDynamicLight();

    boolean lambdynlights_updateDynamicLight(@NotNull WorldRenderer renderer);

    void lambdynlights_scheduleTrackedChunksRebuild(@NotNull WorldRenderer renderer);
}
