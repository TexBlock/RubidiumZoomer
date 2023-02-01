package org.thinkingstudio.rubidium_toolkit.mixins.Sodium;

import com.google.common.collect.ImmutableList;
import org.thinkingstudio.rubidium_toolkit.config.ConfigEnum;
import org.thinkingstudio.rubidium_toolkit.config.RubidiumToolkitConfig;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(SodiumOptionsGUI.class)
public abstract class ZoomOptionsPage
{

    @Shadow
    @Final
    private List<OptionPage> pages;
    private static final SodiumOptionsStorage sodiumOpts = new SodiumOptionsStorage();


    @Inject(method = "<init>", at = @At("RETURN"))
    private void Zoom(Screen prevScreen, CallbackInfo ci) {
        List<OptionGroup> groups = new ArrayList<>();

        OptionImpl<SodiumGameOptions, Boolean> lowerSensitivity = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.lower_sensitivity.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.lower_sensitivity.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> RubidiumToolkitConfig.lowerZoomSensitivity.set(value),
                        (options) -> RubidiumToolkitConfig.lowerZoomSensitivity.get())
                .setImpact(OptionImpact.LOW)
                .build();

        OptionImpl<SodiumGameOptions, Boolean> zoomScrolling = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.scrolling.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.scrolling.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> RubidiumToolkitConfig.zoomScrolling.set(value),
                        (options) -> RubidiumToolkitConfig.zoomScrolling.get())
                .setImpact(OptionImpact.LOW)
                .build();

        OptionImpl<SodiumGameOptions, Boolean> zoomOverlay = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.overlay.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.overlay.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> RubidiumToolkitConfig.zoomOverlay.set(value),
                        (options) -> RubidiumToolkitConfig.zoomOverlay.get())
                .setImpact(OptionImpact.LOW)
                .build();

        groups.add(OptionGroup
                .createBuilder()
                .add(lowerSensitivity)
                .add(zoomScrolling)
                .add(zoomOverlay)
                .build()
        );



        Option<ConfigEnum.ZoomTransitionOptions> zoomTransition =  OptionImpl.createBuilder(ConfigEnum.ZoomTransitionOptions.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.transition.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.transition.tooltip"))
                .setControl(
                        (option) -> new CyclingControl<>(option, ConfigEnum.ZoomTransitionOptions.class, new String[] {
                        I18n.get("rubidium_toolkit.options.off"),
                        I18n.get("rubidium_toolkit.options.smooth")
                        }
                    )
                )
                .setBinding(
                        (opts, value) -> RubidiumToolkitConfig.zoomTransition.set(value.toString()),
                        (opts) -> ConfigEnum.ZoomTransitionOptions.valueOf(RubidiumToolkitConfig.zoomTransition.get()))
                .setImpact(OptionImpact.LOW)
                .build();

        Option<ConfigEnum.ZoomModes> zoomMode =  OptionImpl.createBuilder(ConfigEnum.ZoomModes.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.keybind.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.keybind.tooltip"))
                .setControl(
                        (option) -> new CyclingControl<>(option, ConfigEnum.ZoomModes.class, new String[] {
                        I18n.get("rubidium_toolkit.options.hold"),
                        I18n.get("rubidium_toolkit.options.toggle"),
                        I18n.get("rubidium_toolkit.options.persistent")
                        }
                    )
                )
                .setBinding(
                        (opts, value) -> RubidiumToolkitConfig.zoomMode.set(value.toString()),
                        (opts) -> ConfigEnum.ZoomModes.valueOf(RubidiumToolkitConfig.zoomMode.get()))
                .setImpact(OptionImpact.LOW)
                .build();

        Option<ConfigEnum.CinematicCameraOptions> cinematicCameraMode =  OptionImpl.createBuilder(ConfigEnum.CinematicCameraOptions.class, sodiumOpts)
                .setName(I18n.get("rubidium_toolkit.zoom.cinematic_camera.name"))
                .setTooltip(I18n.get("rubidium_toolkit.zoom.cinematic_camera.tooltip"))
                .setControl((option) -> new CyclingControl<>(option, ConfigEnum.CinematicCameraOptions.class, new String[] {
                        I18n.get("rubidium_toolkit.options.off"),
                        I18n.get("rubidium_toolkit.options.vanilla"),
                        I18n.get("rubidium_toolkit.options.multiplied")
                        }
                    )
                )
                .setBinding(
                        (opts, value) -> RubidiumToolkitConfig.cinematicCameraMode.set(value.toString()),
                        (opts) -> ConfigEnum.CinematicCameraOptions.valueOf(RubidiumToolkitConfig.cinematicCameraMode.get()))
                .setImpact(OptionImpact.LOW)
                .build();

        groups.add(OptionGroup
                .createBuilder()
                .add(zoomTransition)
                .add(zoomMode)
                .add(cinematicCameraMode)
                .build()
        );


        pages.add(new OptionPage(I18n.get("rubidium_toolkit.zoom.options.name"), ImmutableList.copyOf(groups)));
    }
}