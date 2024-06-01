package co.secretonline.accessiblestep;

import java.util.Arrays;

import com.mojang.serialization.Codec;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

public class AccessibleStepOptions {
	private static final Text OFF_TOOLTIP = Text.translatable("options.accessiblestep.off.tooltip");
	private static final Text STEP_TOOLTIP = Text.translatable("options.accessiblestep.step.tooltip");
	private static final Text AUTO_JUMP_TOOLTIP = Text.translatable("options.accessiblestep.autojump.tooltip");

	private static final SimpleOption<AccessibleStepOptionMode> stepOption = new SimpleOption<AccessibleStepOptionMode>(
			"options.accessiblestep.option",
			(value) -> switch (value) {
				case AccessibleStepOptionMode.OFF -> Tooltip.of(OFF_TOOLTIP);
				case AccessibleStepOptionMode.STEP -> Tooltip.of(STEP_TOOLTIP);
				case AccessibleStepOptionMode.AUTO_JUMP -> Tooltip.of(AUTO_JUMP_TOOLTIP);
				default -> throw new MatchException(null, null);
			},
			SimpleOption.enumValueText(),
			new SimpleOption.PotentialValuesBasedCallbacks<AccessibleStepOptionMode>(
					Arrays.asList(AccessibleStepOptionMode.values()),
					AccessibleStepOptionMode.CODEC),
			AccessibleStepOptionMode.OFF,
			AccessibleStepOptions::onStepOptionChange);

	private static void onStepOptionChange(AccessibleStepOptionMode value) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Also update auto-jump option behind the scenes
		if (value == AccessibleStepOptionMode.AUTO_JUMP) {
			client.options.getAutoJump().setValue(true);
		} else {
			client.options.getAutoJump().setValue(false);
		}
	}

	public static SimpleOption<AccessibleStepOptionMode> getStepOption() {
		return stepOption;
	}

	public enum AccessibleStepOptionMode implements TranslatableOption, StringIdentifiable {
		OFF(0, "false", "options.off"),
		STEP(1, "step", "options.accessiblestep.step"),
		AUTO_JUMP(2, "autojump", "options.autoJump");

		public static final Codec<AccessibleStepOptionMode> CODEC;
		private final int id;
		private final String serializedId;
		private final String translationKey;

		private AccessibleStepOptionMode(int id, String serializedId, String translationKey) {
			this.id = id;
			this.serializedId = serializedId;
			this.translationKey = translationKey;
		}

		public String asString() {
			return this.serializedId;
		}

		public int getId() {
			return this.id;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		static {
			CODEC = StringIdentifiable.createCodec(AccessibleStepOptionMode::values);
		}
	}
}
