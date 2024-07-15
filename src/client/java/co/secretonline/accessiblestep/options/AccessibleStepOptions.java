package co.secretonline.accessiblestep.options;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import co.secretonline.accessiblestep.modmenu.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.DoubleSliderCallbacks;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;

public class AccessibleStepOptions {
	public static final String STEP_MODE_OPTIONS_KEY = "accessibleStep";
	public static final String FULL_RANGE_OPTIONS_KEY = "accessibleStepFullRange";
	public static final String STEP_HEIGHT_OPTIONS_KEY = "accessibleStepHeight";
	public static final String SNEAK_HEIGHT_OPTIONS_KEY = "accessibleSneakHeight";
	public static final String SPRINT_HEIGHT_OPTIONS_KEY = "accessibleSprintHeight";

	public static final double MIN_STEP_HEIGHT = 0.0;
	public static final double MAX_STEP_HEIGHT_NORMAL = 2.5;
	public static final double MAX_STEP_HEIGHT_FULL = 10.0;
	// It'd probably be more accurate to use 16 increments as that maps to a pixel;
	// the smallest imcrement in vanilla. However, that leads to options
	// values that have up to 4 decimal places which just doesn't look nice.
	// An alternative could be to format the step heights as mixed fractions
	// (e.g. 1¼ or even having 16 as the denominator in all cases), but that strays
	// further from Minecraft's formatting style.
	private static final double STEP_HEIGHT_INCREMENTS_PER_BLOCK = 20;

	public static final double VANILLA_STEP_HEIGHT = EntityAttributes.GENERIC_STEP_HEIGHT.value().getDefaultValue();
	private static final double MOD_DEFAULT_STEP_HEIGHT = 1.25;
	private static final double MOD_DEFAULT_SNEAK_HEIGHT = VANILLA_STEP_HEIGHT;
	private static final double MOD_DEFAULT_SPRINT_HEIGHT = MOD_DEFAULT_STEP_HEIGHT;

	private static final Text STEP_MODE_OFF_TOOLTIP = Text.translatable("options.accessiblestep.off.tooltip");
	private static final Text STEP_MODE_STEP_TOOLTIP = Text.translatable("options.accessiblestep.step.tooltip");
	private static final Text STEP_MODE_AUTO_JUMP_TOOLTIP = Text.translatable("options.accessiblestep.autojump.tooltip");

	private static final Text FULL_RANGE_TOOLTIP = Text.translatable("options.accessiblestep.fullrange.tooltip");
	private static final Text STEP_HEIGHT_TOOLTIP = Text.translatable("options.accessiblestep.height.tooltip");
	private static final Text SNEAK_HEIGHT_TOOLTIP = Text.translatable("options.accessiblestep.sneakheight.tooltip");
	private static final Text SPRINT_HEIGHT_TOOLTIP = Text.translatable("options.accessiblestep.sprintheight.tooltip");

	private static final SimpleOption<StepMode> stepModeOption = new SimpleOption<StepMode>(
			"options.accessiblestep.mode",
			AccessibleStepOptions::getStepModeTooltip,
			SimpleOption.enumValueText(),
			new SimpleOption.PotentialValuesBasedCallbacks<StepMode>(
					Arrays.asList(StepMode.values()),
					StepMode.CODEC),
			StepMode.OFF,
			AccessibleStepOptions::onStepModeChange);

	private static Tooltip getStepModeTooltip(StepMode value) {
		switch (value) {
			case OFF:
				return Tooltip.of(STEP_MODE_OFF_TOOLTIP);
			case STEP:
				return Tooltip.of(STEP_MODE_STEP_TOOLTIP);
			case AUTO_JUMP:
				return Tooltip.of(STEP_MODE_AUTO_JUMP_TOOLTIP);
			default:
				throw new IncompatibleClassChangeError();
		}
	}

	private static void onStepModeChange(StepMode value) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Also update auto-jump option behind the scenes
		if (value == StepMode.AUTO_JUMP) {
			client.options.getAutoJump().setValue(true);
		} else {
			client.options.getAutoJump().setValue(false);
		}
	}

	private static final SimpleOption<Boolean> fullRangeOption = SimpleOption.ofBoolean(
			"options.accessiblestep.fullrange",
			(Boolean value) -> Tooltip.of(FULL_RANGE_TOOLTIP),
			false,
			AccessibleStepOptions::onFullRangeChange);

	private static void onFullRangeChange(Boolean value) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Rescale sliders on the options page to have the correct visible value.
		// Without this, the sliders enter a broken state where:
		// 1. The text in the slider shows the correct value.
		// 2. The handle of the slider is in its old position.
		// 3. Saving the option uses the handle position, so it saves a different value
		// to what is shown.
		if (client.currentScreen instanceof AccessibleStepOptionsScreen) {
			AccessibleStepOptionsScreen optionsScreen = (AccessibleStepOptionsScreen) client.currentScreen;
			optionsScreen.rescaleStepHeightSliders();
		}
	}

	private static final SimpleOption<Double> stepHeightOption = new SimpleOption<Double>(
			"options.accessiblestep.height",
			(Double value) -> Tooltip.of(STEP_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			DoubleSliderCallbacks.INSTANCE.withModifier(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			MOD_DEFAULT_STEP_HEIGHT,
			(value) -> {
			});

	private static final SimpleOption<Double> sneakHeightOption = new SimpleOption<Double>(
			"options.accessiblestep.sneakheight",
			(Double value) -> Tooltip.of(SNEAK_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			DoubleSliderCallbacks.INSTANCE.withModifier(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			MOD_DEFAULT_SNEAK_HEIGHT,
			(value) -> {
			});

	private static final SimpleOption<Double> sprintHeightOption = new SimpleOption<Double>(
			"options.accessiblestep.sprintheight",
			(Double value) -> Tooltip.of(SPRINT_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			DoubleSliderCallbacks.INSTANCE.withModifier(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			MOD_DEFAULT_SPRINT_HEIGHT,
			(value) -> {
			});

	private static double toStepHeight(double rangeValue) {
		double currentMaxHeight = fullRangeOption.getValue().booleanValue() ? MAX_STEP_HEIGHT_FULL : MAX_STEP_HEIGHT_NORMAL;

		return toStepHeight(rangeValue, currentMaxHeight);
	}

	private static double toStepHeight(double rangeValue, double maxValue) {
		double mappedValue = rangeValue * maxValue;
		// Limit to 0.05 block increments so the slider doesn't go crazy
		return Math.floor(mappedValue * STEP_HEIGHT_INCREMENTS_PER_BLOCK) /
				STEP_HEIGHT_INCREMENTS_PER_BLOCK;
	}

	private static double fromStepHeight(double stepHeight) {
		double currentMaxHeight = fullRangeOption.getValue().booleanValue() ? MAX_STEP_HEIGHT_FULL : MAX_STEP_HEIGHT_NORMAL;

		return fromStepHeight(stepHeight, currentMaxHeight);
	}

	private static double fromStepHeight(double stepHeight, double maxValue) {
		// No increments checking required here
		return stepHeight / maxValue;
	}

	private static Text getStepHeightText(Text optionText, Double value) {
		return Text.translatable("options.generic_value", new Object[] { optionText, value });
	}

	public static SimpleOption<StepMode> getStepModeOption() {
		return stepModeOption;
	}

	public static SimpleOption<Boolean> getFullRangeOption() {
		return fullRangeOption;
	}

	public static SimpleOption<Double> getStepHeightOption() {
		return stepHeightOption;
	}

	public static SimpleOption<Double> getSneakHeightOption() {
		return sneakHeightOption;
	}

	public static SimpleOption<Double> getSprintHeightOption() {
		return sprintHeightOption;
	}
}
