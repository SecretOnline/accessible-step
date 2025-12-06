package co.secretonline.accessiblestep.screen;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.OptionInstance.UnitDouble;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class AccessibleStepOptions {
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

	private static final Component STEP_MODE_OFF_TOOLTIP = Component.translatable("options.accessiblestep.off.tooltip");
	private static final Component STEP_MODE_STEP_TOOLTIP = Component.translatable("options.accessiblestep.step.tooltip");
	private static final Component STEP_MODE_AUTO_JUMP_TOOLTIP = Component
			.translatable("options.accessiblestep.autojump.tooltip");

	private static final Component PER_WORLD_TOOLTIP = Component.translatable("options.accessiblestep.perworld.tooltip");
	private static final Component FULL_RANGE_TOOLTIP = Component
			.translatable("options.accessiblestep.fullrange.tooltip");
	private static final Component STEP_HEIGHT_TOOLTIP = Component.translatable("options.accessiblestep.height.tooltip");
	private static final Component SNEAK_HEIGHT_TOOLTIP = Component
			.translatable("options.accessiblestep.sneakheight.tooltip");
	private static final Component SPRINT_HEIGHT_TOOLTIP = Component
			.translatable("options.accessiblestep.sprintheight.tooltip");

	private static final OptionInstance<StepMode> stepModeOption = new OptionInstance<StepMode>(
			"options.accessiblestep.mode",
			AccessibleStepOptions::getStepModeTooltip,
			OptionInstance.forOptionEnum(),
			new OptionInstance.Enum<StepMode>(
					Arrays.asList(StepMode.values()),
					StepMode.CODEC),
			StepMode.OFF,
			AccessibleStepOptions::onStepModeChange);

	private static Tooltip getStepModeTooltip(StepMode value) {
		switch (value) {
			case OFF:
				return Tooltip.create(STEP_MODE_OFF_TOOLTIP);
			case STEP:
				return Tooltip.create(STEP_MODE_STEP_TOOLTIP);
			case AUTO_JUMP:
				return Tooltip.create(STEP_MODE_AUTO_JUMP_TOOLTIP);
			default:
				throw new IncompatibleClassChangeError();
		}
	}

	private static void onStepModeChange(StepMode value) {
		State.config.setStepMode(value);
	}

	private static final OptionInstance<Boolean> perWorldOption = OptionInstance.createBoolean(
			"options.accessiblestep.perworld",
			(Boolean value) -> Tooltip.create(PER_WORLD_TOOLTIP),
			false,
			AccessibleStepOptions::onPerWorldChange);

	private static void onPerWorldChange(Boolean value) {
		State.config.setHasConfigForWorld(value);

		// Since the world's config has changed, we need to update all of the widgets on
		// the screen.
		Minecraft client = Minecraft.getInstance();
		if (client.screen instanceof AccessibleStepOptionsScreen optionsScreen) {
			optionsScreen.resetOptionsForWorld();
		}
	}

	private static final OptionInstance<Boolean> fullRangeOption = OptionInstance.createBoolean(
			"options.accessiblestep.fullrange",
			(Boolean value) -> Tooltip.create(FULL_RANGE_TOOLTIP),
			false,
			AccessibleStepOptions::onFullRangeChange);

	private static void onFullRangeChange(Boolean value) {
		State.config.setFullRange(value);

		// Rescale sliders on the options page to have the correct visible value.
		// Without this, the sliders enter a broken state where:
		// 1. The text in the slider shows the correct value.
		// 2. The handle of the slider is in its old position.
		// 3. Saving the option uses the handle position, so it saves a different value
		// to what is shown.
		Minecraft client = Minecraft.getInstance();
		if (client.screen instanceof AccessibleStepOptionsScreen optionsScreen) {
			optionsScreen.rescaleStepHeightSliders();
		}
	}

	private static final OptionInstance<Double> stepHeightOption = new OptionInstance<Double>(
			"options.accessiblestep.height",
			(Double value) -> Tooltip.create(STEP_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			UnitDouble.INSTANCE.xmap(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			Constants.MOD_DEFAULT_STEP_HEIGHT,
			(value) -> {
				State.config.setStepHeight(value);
			});

	private static final OptionInstance<Double> sneakHeightOption = new OptionInstance<Double>(
			"options.accessiblestep.sneakheight",
			(Double value) -> Tooltip.create(SNEAK_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			UnitDouble.INSTANCE.xmap(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			Constants.MOD_DEFAULT_SNEAK_HEIGHT,
			(value) -> {
				State.config.setSneakHeight(value);
			});

	private static final OptionInstance<Double> sprintHeightOption = new OptionInstance<Double>(
			"options.accessiblestep.sprintheight",
			(Double value) -> Tooltip.create(SPRINT_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getStepHeightText,
			UnitDouble.INSTANCE.xmap(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(MIN_STEP_HEIGHT, MAX_STEP_HEIGHT_FULL),
			Constants.MOD_DEFAULT_SPRINT_HEIGHT,
			(value) -> {
				State.config.setSprintHeight(value);
			});

	private static double toStepHeight(double rangeValue) {
		double currentMaxHeight = fullRangeOption.get().booleanValue() ? MAX_STEP_HEIGHT_FULL : MAX_STEP_HEIGHT_NORMAL;

		return toStepHeight(rangeValue, currentMaxHeight);
	}

	private static double toStepHeight(double rangeValue, double maxValue) {
		double mappedValue = rangeValue * maxValue;
		// Limit to 0.05 block increments so the slider doesn't go crazy
		return Math.floor(mappedValue * STEP_HEIGHT_INCREMENTS_PER_BLOCK) /
				STEP_HEIGHT_INCREMENTS_PER_BLOCK;
	}

	private static double fromStepHeight(double stepHeight) {
		double currentMaxHeight = fullRangeOption.get().booleanValue() ? MAX_STEP_HEIGHT_FULL : MAX_STEP_HEIGHT_NORMAL;

		return fromStepHeight(stepHeight, currentMaxHeight);
	}

	private static double fromStepHeight(double stepHeight, double maxValue) {
		// No increments checking required here
		return stepHeight / maxValue;
	}

	private static Component getStepHeightText(Component optionText, Double value) {
		return Component.translatable("options.generic_value", new Object[] { optionText, value });
	}

	// These methods also update the option's value to the current value.

	public static OptionInstance<StepMode> getStepModeOption() {
		stepModeOption.set(State.config.getStepMode());
		return stepModeOption;
	}

	public static OptionInstance<Boolean> getPerWorldOption() {
		perWorldOption.set(State.config.hasConfigForWorld());
		return perWorldOption;
	}

	public static OptionInstance<Boolean> getFullRangeOption() {
		fullRangeOption.set(State.config.getFullRange());
		return fullRangeOption;
	}

	public static OptionInstance<Double> getStepHeightOption() {
		stepHeightOption.set(State.config.getStepHeight());
		return stepHeightOption;
	}

	public static OptionInstance<Double> getSneakHeightOption() {
		sneakHeightOption.set(State.config.getSneakHeight());
		return sneakHeightOption;
	}

	public static OptionInstance<Double> getSprintHeightOption() {
		sprintHeightOption.set(State.config.getSprintHeight());
		return sprintHeightOption;
	}
}
