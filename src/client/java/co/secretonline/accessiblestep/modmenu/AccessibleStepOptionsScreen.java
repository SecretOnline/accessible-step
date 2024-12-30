package co.secretonline.accessiblestep.modmenu;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.mixin.client.SliderWidgetAccessor;
import co.secretonline.accessiblestep.options.AccessibleStepConfig;
import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptionsScreen extends GameOptionsScreen {
	private static SimpleOption<?>[] getGlobalOptions() {
		return new SimpleOption[] {
				AccessibleStepOptions.getPerWorldOption(),
				AccessibleStepOptions.getFullRangeOption(),
		};
	}

	private static SimpleOption<?>[] getWorldOptions() {
		return new SimpleOption[] {
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
				Text.translatable("options.accessiblestep.title"));
	}

	@Override
	protected void addOptions() {
		this.body.addAll(getGlobalOptions());
		// TODO: Add title between sections
		this.body.addAll(getWorldOptions());

		// The values in the options may not be correct as the player might have
		// switched worlds before opening the screen. As such we need to reset the
		// values of each option to the currently correct value.
		this.resetOptionsForWorld();
	}

	public void resetOptionsForWorld() {
		// Update per world option
		AccessibleStepOptions.getPerWorldOption().setValue(State.config.hasConfigForWorld());
		// Also need to disable the button if no world is selected
		var widget = this.body.getWidgetFor(AccessibleStepOptions.getPerWorldOption());
		if (widget != null && widget instanceof PressableWidget) {

			if (State.worldName == null) {
				((PressableWidget) widget).active = false;
			} else {
				((PressableWidget) widget).active = true;
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
		var widget = this.body.getWidgetFor(option);
		if (widget != null && widget instanceof CyclingButtonWidget<?>) {
			@SuppressWarnings("unchecked")
			CyclingButtonWidget<T> button = (CyclingButtonWidget<T>) widget;

			button.setValue(option.getValue());
		}
	}

	private void resetSliderWidget(SimpleOption<Double> option, double maxHeight) {
		var widget = this.body.getWidgetFor(option);

		if (widget != null && widget instanceof SliderWidget) {
			// Widget needs value in range of 0-1 instead of option's value
			((SliderWidgetAccessor) widget).invokeSetValue(option.getValue() / maxHeight);
		}
	}
}
