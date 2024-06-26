package co.secretonline.accessiblestep.options;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.DoubleSliderCallbacks;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;

public class AccessibleStepOptions {
	public static final String STEP_MODE_OPTIONS_KEY = "accessibleStep";
	public static final String STEP_HEIGHT_OPTIONS_KEY = "accessibleStepHeight";
	public static final String SNEAK_HEIGHT_OPTIONS_KEY = "accessibleSneakHeight";

	private static final double MIN_STEP_HEIGHT = 0.0;
	private static final double MAX_STEP_HEIGHT = 10.0;
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

	private static final Text STEP_MODE_OFF_TOOLTIP = Text.translatable("options.accessiblestep.off.tooltip");
	private static final Text STEP_MODE_STEP_TOOLTIP = Text.translatable("options.accessiblestep.step.tooltip");
	private static final Text STEP_MODE_AUTO_JUMP_TOOLTIP = Text.translatable("options.accessiblestep.autojump.tooltip");

	private static final Text SNEAK_HEIGHT_TOOLTIP = Text.translatable("options.accessiblestep.sneakheight.tooltip");

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
		return switch (value) {
			case StepMode.OFF -> Tooltip.of(STEP_MODE_OFF_TOOLTIP);
			case StepMode.STEP -> Tooltip.of(STEP_MODE_STEP_TOOLTIP);
			case StepMode.AUTO_JUMP -> Tooltip.of(STEP_MODE_AUTO_JUMP_TOOLTIP);
			default -> throw new MatchException(null, null);
		};
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

	private static final SimpleOption<Double> stepHeightOption = new SimpleOption<Double>(
			"options.accessiblestep.height",
			SimpleOption.emptyTooltip(),
			AccessibleStepOptions::getStepHeightText,
			DoubleSliderCallbacks.INSTANCE.withModifier(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(0.0, 10.0),
			MOD_DEFAULT_STEP_HEIGHT,
			(value) -> {
			});

	private static final SimpleOption<Double> sneakHeightOption = new SimpleOption<Double>(
			"options.accessiblestep.sneakheight",
			(Double value) -> Tooltip.of(SNEAK_HEIGHT_TOOLTIP),
			AccessibleStepOptions::getSneakHeightText,
			DoubleSliderCallbacks.INSTANCE.withModifier(
					AccessibleStepOptions::toStepHeight,
					AccessibleStepOptions::fromStepHeight),
			Codec.doubleRange(0.0, 10.0),
			MOD_DEFAULT_SNEAK_HEIGHT,
			(value) -> {
			});

	private static double toStepHeight(double rangeValue) {
		double reducedRange = (rangeValue * (MAX_STEP_HEIGHT - MIN_STEP_HEIGHT)) +
				MIN_STEP_HEIGHT;
		// Limit to 0.05 block increments so the slider doesn't go crazy
		return Math.floor(reducedRange * STEP_HEIGHT_INCREMENTS_PER_BLOCK) /
				STEP_HEIGHT_INCREMENTS_PER_BLOCK;
	}

	private static double fromStepHeight(double stepHeight) {
		// No increments checking required here
		return (stepHeight - MIN_STEP_HEIGHT) / (MAX_STEP_HEIGHT - MIN_STEP_HEIGHT);
	}

	private static Text getStepHeightText(Text optionText, Double value) {
		Object displayValue = value;
		if (value == MOD_DEFAULT_STEP_HEIGHT) {
			displayValue = Text.translatable("options.accessiblestep.default.mod");
		} else if (value == VANILLA_STEP_HEIGHT) {
			displayValue = Text.translatable("options.accessiblestep.default.vanilla");
		}

		return Text.translatable("options.generic_value", new Object[] { optionText, displayValue });
	}

	private static Text getSneakHeightText(Text optionText, Double value) {
		Object displayValue = value;
		if (value == MOD_DEFAULT_SNEAK_HEIGHT) {
			displayValue = Text.translatable("options.accessiblestep.default.mod");
		} else if (value == VANILLA_STEP_HEIGHT) {
			displayValue = Text.translatable("options.accessiblestep.default.vanilla");
		}

		return Text.translatable("options.generic_value", new Object[] { optionText, displayValue });
	}

	public static SimpleOption<StepMode> getStepModeOption() {
		return stepModeOption;
	}

	public static SimpleOption<Double> getStepHeightOption() {
		return stepHeightOption;
	}

	public static SimpleOption<Double> getSneakHeightOption() {
		return sneakHeightOption;
	}
}
