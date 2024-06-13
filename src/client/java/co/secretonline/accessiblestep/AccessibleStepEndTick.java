package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.AccessibleStepOptions.AccessibleStepOptionMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

public class AccessibleStepEndTick implements EndTick {

	private static final float STEP_HEIGHT_BASE = 0.6f;

	/**
	 * Default step height is 0.6 blocks.
	 * From testing with other step mods, modified step heights are usually around
	 * 1.25 blocks.
	 */
	private static final float STEP_HEIGHT_MODIFIER_AMOUNT = 0.65f;

	@Override
	public void onEndTick(MinecraftClient client) {
		if (client.player == null) {
			return;
		}

		SimpleOption<AccessibleStepOptionMode> accessibleStepOption = AccessibleStepOptions.getStepOption();

		if (accessibleStepOption.getValue().equals(AccessibleStepOptionMode.STEP)) {
			if (client.player.isSneaking()) {
				client.player.setStepHeight(STEP_HEIGHT_BASE);
			} else {
				client.player.setStepHeight(STEP_HEIGHT_BASE + STEP_HEIGHT_MODIFIER_AMOUNT);
			}
		} else {
			client.player.setStepHeight(STEP_HEIGHT_BASE);
		}
	}
}
