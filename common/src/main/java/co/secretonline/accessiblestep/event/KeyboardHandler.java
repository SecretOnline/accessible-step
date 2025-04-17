package co.secretonline.accessiblestep.event;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KeyboardHandler {
	public void onEndTick(MinecraftClient client) {
		StepMode currentMode = State.config.getStepMode();

		int modeId = currentMode.getId();
		while (AccessibleStepCommon.STEP_MODE_KEY_BINDING.wasPressed()) {
			modeId = (modeId + 1) % StepMode.values().length;
		}

		if (modeId != currentMode.getId()) {
			StepMode newMode = StepMode.byId(modeId);
			State.config.setStepMode(newMode);

			String valueColor = (newMode.equals(StepMode.OFF) ? Formatting.RED : Formatting.GREEN).toString();
			String valueString = valueColor + Text.translatable(newMode.getTranslationKey()).getString() + Formatting.RESET;
			client.player.sendMessage(
					Text.translatable("options.generic_value",
							new Object[] { Text.translatable("options.accessiblestep.mode"), valueString }),
					true);
		}
	}
}
