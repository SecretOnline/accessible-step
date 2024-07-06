package co.secretonline.accessiblestep.modmenu;

import co.secretonline.accessiblestep.mixin.client.SliderWidgetAccessor;
import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptionsScreen extends GameOptionsScreen {
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[] {
				AccessibleStepOptions.getStepModeOption(),
				AccessibleStepOptions.getFullRangeOption(),
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
		this.body.addAll(getOptions(this.gameOptions));
	}

	public void rescaleStepHeightSliders() {
		boolean isFullRange = AccessibleStepOptions.getFullRangeOption().getValue();
		double newMaxHeight = isFullRange
				? AccessibleStepOptions.MAX_STEP_HEIGHT_FULL
				: AccessibleStepOptions.MAX_STEP_HEIGHT_NORMAL;

		@SuppressWarnings("unchecked")
		SimpleOption<Double>[] sliderOptions = new SimpleOption[] {
				AccessibleStepOptions.getStepHeightOption(),
				AccessibleStepOptions.getSneakHeightOption(),
				AccessibleStepOptions.getSprintHeightOption(),
		};

		for (SimpleOption<Double> option : sliderOptions) {
			var widget = this.body.getWidgetFor(option);

			if (widget instanceof SliderWidget) {
				// Widget needs value in range of 0-1 instead of option's value
				((SliderWidgetAccessor) widget).invokeSetValue(option.getValue() / newMaxHeight);
			}
		}
	}
}
