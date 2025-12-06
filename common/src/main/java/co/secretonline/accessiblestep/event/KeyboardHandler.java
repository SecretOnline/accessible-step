package co.secretonline.accessiblestep.event;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class KeyboardHandler {
	public void onEndTick(Minecraft client) {
		StepMode currentMode = State.config.getStepMode();

		int modeId = currentMode.getId();
		while (AccessibleStepCommon.STEP_MODE_KEY_BINDING.consumeClick()) {
			modeId = (modeId + 1) % StepMode.values().length;
		}

		if (modeId != currentMode.getId()) {
			StepMode newMode = StepMode.byId(modeId);
			State.config.setStepMode(newMode);

			String valueColor = (newMode.equals(StepMode.OFF) ? ChatFormatting.RED : ChatFormatting.GREEN).toString();
			String valueString = valueColor + Component.translatable(newMode.getKey()).getString() + ChatFormatting.RESET;
			client.player.displayClientMessage(
					Component.translatable("options.generic_value",
							new Object[] { Component.translatable("options.accessiblestep.mode"), valueString }),
					true);
		}
	}
}
