package co.secretonline.accessiblestep;

import org.lwjgl.glfw.GLFW;

import co.secretonline.accessiblestep.options.StepMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AccessibleStepKeyboardHandlers {
	public static KeyBinding keyBinding = new KeyBinding(
			"key.accessiblestep.mode",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"category.accessiblestep.title");

	public void onEndTick(MinecraftClient client) {
		StepMode currentMode = State.config.getStepMode();

		int modeId = currentMode.getId();
		while (keyBinding.wasPressed()) {
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
