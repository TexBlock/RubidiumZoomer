package org.thinkingstudio.rubidium_toolkit.features.dynamic_lights.api;

import org.thinkingstudio.rubidium_toolkit.features.dynamic_lights.RubidiumDynLights;
import org.thinkingstudio.rubidium_toolkit.features.dynamic_lights.config.DynamicLightsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Represents a dynamic light handler.
 *
 * @param <T> The type of the light source.
 * @author LambdAurora
 * @version 1.3.0
 * @since 1.1.0
 */
public interface DynamicLightHandler<T> {
	/**
	 * Returns the luminance of the light source.
	 *
	 * @param lightSource The light source.
	 * @return The luminance.
	 */
	int getLuminance(T lightSource);

	/**
	 * Returns whether the light source is water-sensitive or not.
	 *
	 * @param lightSource The light source.
	 * @return True if the light source is water-sensitive, else false.
	 */
	default boolean isWaterSensitive(T lightSource) {
		return false;
	}

	/**
	 * Returns a dynamic light handler.
	 *
	 * @param luminance The luminance function.
	 * @param waterSensitive The water sensitive function.
	 * @param <T> The type of the entity.
	 * @return The completed handler.
	 */
	static <T extends LivingEntity> @NotNull DynamicLightHandler<T> makeHandler(Function<T, Integer> luminance, Function<T, Boolean> waterSensitive) {
		return new DynamicLightHandler<>() {
			@Override
			public int getLuminance(T lightSource) {
				return luminance.apply(lightSource);
			}

			@Override
			public boolean isWaterSensitive(T lightSource) {
				return waterSensitive.apply(lightSource);
			}
		};
	}

	/**
	 * Returns a living entity dynamic light handler.
	 *
	 * @param handler The handler.
	 * @param <T> The type of the entity.
	 * @return The completed handler.
	 */
	static <T extends LivingEntity> @NotNull DynamicLightHandler<T> makeLivingEntityHandler(@NotNull DynamicLightHandler<T> handler) {
		return entity -> {
			int luminance = 0;
			for (var equipped : entity.getAllSlots()) {
				luminance = Math.max(luminance, RubidiumDynLights.getLuminanceFromItemStack(equipped, entity.isUnderWater()));
			}
			return Math.max(luminance, handler.getLuminance(entity));
		};
	}

	/**
	 * Returns a Creeper dynamic light handler.
	 *
	 * @param handler Extra handler.
	 * @param <T> The type of Creeper entity.
	 * @return The completed handler.
	 */
	static <T extends Creeper> @NotNull DynamicLightHandler<T> makeCreeperEntityHandler(@Nullable DynamicLightHandler<T> handler) {
		return new DynamicLightHandler<>() {
			@Override
			public int getLuminance(T entity) {
				int luminance = 0;

				if (entity.getSwelling(0.f) > 0.001) {
					luminance = switch (DynamicLightsConfig.Quality.get()) {
						case OFF -> 0;
						case SLOW, FAST -> 10;
						case REALTIME -> (int) (entity.getSwelling(0.f) * 10.0);
					};
				}

				if (handler != null)
					luminance = Math.max(luminance, handler.getLuminance(entity));

				return luminance;
			}

			@Override
			public boolean isWaterSensitive(T lightSource) {
				return true;
			}
		};
	}
}
