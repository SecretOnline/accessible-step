package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AccessibleStepEndTick implements EndTick {
	@Override
	public void onEndTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;

		if (player == null) {
			return;
		}

		StepMode stepMode = AccessibleStepOptions.getStepModeOption().getValue();

		if (stepMode.equals(StepMode.STEP)) {
			double stepHeight = AccessibleStepOptions.getStepHeightOption().getValue();

			if (player.isSneaking()) {
				double sneakHeight = AccessibleStepOptions.getSneakHeightOption().getValue();
				double heightToSet = Math.min(stepHeight, sneakHeight);

				this.setStepHeight(player, heightToSet);
			} else if (player.isSprinting() || client.options.sprintKey.isPressed()) {
				double sprintHeight = AccessibleStepOptions.getSprintHeightOption().getValue();
				double heightToSet = Math.max(stepHeight, sprintHeight);

				this.setStepHeight(player, heightToSet);
			} else {
				this.setStepHeight(player, stepHeight);
			}
		} else {
			this.setStepHeight(player, AccessibleStepOptions.VANILLA_STEP_HEIGHT);
		}
	}

	private void setStepHeight(ClientPlayerEntity player, double height) {
		player.setStepHeight((float) height);
	}
}
