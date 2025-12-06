package co.secretonline.accessiblestep.screen;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.config.AccessibleStepConfig;
import co.secretonline.accessiblestep.mixin.AbstractSliderButtonAccessor;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class AccessibleStepOptionsScreen extends OptionsSubScreen {
	private static OptionInstance<?>[] getOptions() {
		return new OptionInstance[] {
				AccessibleStepOptions.getPerWorldOption(),
				AccessibleStepOptions.getFullRangeOption(),
				AccessibleStepOptions.getStepModeOption(),
				AccessibleStepOptions.getStepHeightOption(),
				AccessibleStepOptions.getSneakHeightOption(),
				AccessibleStepOptions.getSprintHeightOption(),
		};
	}

	public AccessibleStepOptionsScreen(Screen parent, Options gameOptions) {
		super(
				parent,
				gameOptions,
				Component.translatable("options.accessiblestep.title"));
	}

	@Override
	protected void addOptions() {
		this.list.addSmall(getOptions());

		// The values in the options may not be correct as the player might have
		// switched worlds before opening the screen. As such we need to reset the
		// values of each option and widget to the currently correct value.
		this.resetOptionsForWorld();
	}

	public void resetOptionsForWorld() {
		// Update per world option
		AccessibleStepOptions.getPerWorldOption().set(State.config.hasConfigForWorld());
		// Also need to disable the button if no world is selected
		var widget = this.list.findOption(AccessibleStepOptions.getPerWorldOption());
		if (widget != null && widget instanceof AbstractButton pressableWidget) {

			if (State.worldName == null) {
				pressableWidget.active = false;
			} else {
				pressableWidget.active = true;
			}
		}

		AccessibleStepOptions.getFullRangeOption().set(State.config.getFullRange());

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getCurrentWorldConfig();
		AccessibleStepOptions.getStepModeOption().set(worldConfig.stepMode);
		AccessibleStepOptions.getStepHeightOption().set(worldConfig.stepHeight);
		AccessibleStepOptions.getSneakHeightOption().set(worldConfig.sneakHeight);
		AccessibleStepOptions.getSprintHeightOption().set(worldConfig.sprintHeight);

		// Now that the SimpleOptions themselves have the correct values, we also need
		// to update the state of the widgets on the screen.
		this.resetWidgets();
	}

	public void rescaleStepHeightSliders() {
		this.resetWidgets();
	}

	private void resetWidgets() {
		this.resetCyclingButtonWidget(AccessibleStepOptions.getPerWorldOption());
		this.resetCyclingButtonWidget(AccessibleStepOptions.getStepModeOption());
		this.resetCyclingButtonWidget(AccessibleStepOptions.getFullRangeOption());

		boolean isFullRange = AccessibleStepOptions.getFullRangeOption().get();
		double maxHeight = isFullRange
				? AccessibleStepOptions.MAX_STEP_HEIGHT_FULL
				: AccessibleStepOptions.MAX_STEP_HEIGHT_NORMAL;

		this.resetSliderWidget(AccessibleStepOptions.getStepHeightOption(), maxHeight);
		this.resetSliderWidget(AccessibleStepOptions.getSneakHeightOption(), maxHeight);
		this.resetSliderWidget(AccessibleStepOptions.getSprintHeightOption(), maxHeight);
	}

	private <T> void resetCyclingButtonWidget(OptionInstance<T> option) {
		var widget = this.list.findOption(option);
		if (widget != null && widget instanceof CycleButton<?>) {
			@SuppressWarnings("unchecked")
			CycleButton<T> button = (CycleButton<T>) widget;

			button.setValue(option.get());
		}
	}

	private void resetSliderWidget(OptionInstance<Double> option, double maxHeight) {
		var widget = this.list.findOption(option);

		if (widget != null && widget instanceof AbstractSliderButton) {
			// Widget needs value in range of 0-1 instead of option's value
			((AbstractSliderButtonAccessor) widget).invokeSetValue(option.get() / maxHeight);
		}
	}
}
