package co.secretonline.accessiblestep;

import org.lwjgl.glfw.GLFW;

import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AccessibleStepKeyboardHandlers implements EndTick {

	private static KeyBinding keyBinding;

	public AccessibleStepKeyboardHandlers() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.accessiblestep.mode",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.accessiblestep.title"));
	}

	@Override
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
