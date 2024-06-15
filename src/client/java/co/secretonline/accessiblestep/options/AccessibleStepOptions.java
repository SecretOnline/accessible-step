package co.secretonline.accessiblestep.options;

import java.util.Arrays;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptions {
	public static final String STEP_MODE_OPTIONS_KEY = "accessibleStep";

	private static final Text STEP_MODE_OFF_TOOLTIP = Text.translatable("options.accessiblestep.off.tooltip");
	private static final Text STEP_MODE_STEP_TOOLTIP = Text.translatable("options.accessiblestep.step.tooltip");
	private static final Text STEP_MODE_AUTO_JUMP_TOOLTIP = Text.translatable("options.accessiblestep.autojump.tooltip");

	private static final SimpleOption<StepMode> stepModeOption = new SimpleOption<StepMode>(
			"options.accessiblestep.option",
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

	public static SimpleOption<StepMode> getStepModeOption() {
		return stepModeOption;
	}
}
