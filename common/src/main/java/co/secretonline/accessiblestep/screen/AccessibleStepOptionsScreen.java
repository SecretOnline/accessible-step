package co.secretonline.accessiblestep.screen;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.config.AccessibleStepConfig;
import co.secretonline.accessiblestep.mixin.SliderWidgetAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptionsScreen extends SimpleOptionsScreen {
	private static SimpleOption<?>[] getOptions() {
		return new SimpleOption[] {
				AccessibleStepOptions.getPerWorldOption(),
				AccessibleStepOptions.getFullRangeOption(),
				AccessibleStepOptions.getStepModeOption(),
				AccessibleStepOptions.getStepHeightOption(),
				AccessibleStepOptions.getSneakHeightOption(),
				AccessibleStepOptions.getSprintHeightOption(),
		};
	}

	public AccessibleStepOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(
				parent,
				gameOptions,
				Text.translatable("options.accessiblestep.title"),
				getOptions());
	}

	@Override
	protected void init() {
		super.init();

		// The values in the options may not be correct as the player might have
		// switched worlds before opening the screen. As such we need to reset the
		// values of each option and widget to the currently correct value.
		this.resetOptionsForWorld();
	}

	public void resetOptionsForWorld() {
		// Update per world option
		AccessibleStepOptions.getPerWorldOption().setValue(State.config.hasConfigForWorld());
		// Also need to disable the button if no world is selected
		var widget = this.buttonList.getWidgetFor(AccessibleStepOptions.getPerWorldOption());
		if (widget != null && widget instanceof PressableWidget pressableWidget) {

			if (State.worldName == null) {
				pressableWidget.active = false;
			} else {
				pressableWidget.active = true;
			}
		}

		AccessibleStepOptions.getFullRangeOption().setValue(State.config.getFullRange());

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getCurrentWorldConfig();
		AccessibleStepOptions.getStepModeOption().setValue(worldConfig.stepMode);
		AccessibleStepOptions.getStepHeightOption().setValue(worldConfig.stepHeight);
		AccessibleStepOptions.getSneakHeightOption().setValue(worldConfig.sneakHeight);
		AccessibleStepOptions.getSprintHeightOption().setValue(worldConfig.sprintHeight);

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

		boolean isFullRange = AccessibleStepOptions.getFullRangeOption().getValue();
		double maxHeight = isFullRange
				? AccessibleStepOptions.MAX_STEP_HEIGHT_FULL
				: AccessibleStepOptions.MAX_STEP_HEIGHT_NORMAL;

		this.resetSliderWidget(AccessibleStepOptions.getStepHeightOption(), maxHeight);
		this.resetSliderWidget(AccessibleStepOptions.getSneakHeightOption(), maxHeight);
		this.resetSliderWidget(AccessibleStepOptions.getSprintHeightOption(), maxHeight);
	}

	private <T> void resetCyclingButtonWidget(SimpleOption<T> option) {
		var widget = this.buttonList.getWidgetFor(option);
		if (widget != null && widget instanceof CyclingButtonWidget<?>) {
			@SuppressWarnings("unchecked")
			CyclingButtonWidget<T> button = (CyclingButtonWidget<T>) widget;

			button.setValue(option.getValue());
		}
	}

	private void resetSliderWidget(SimpleOption<Double> option, double maxHeight) {
		var widget = this.buttonList.getWidgetFor(option);

		if (widget != null && widget instanceof SliderWidget) {
			// Widget needs value in range of 0-1 instead of option's value
			((SliderWidgetAccessor) widget).invokeSetValue(option.getValue() / maxHeight);
		}
	}
}
