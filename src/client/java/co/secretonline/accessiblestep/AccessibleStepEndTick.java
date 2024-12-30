package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.options.AccessibleStepConfig;
import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class AccessibleStepEndTick implements EndTick {
	@Override
	public void onEndTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;

		if (player == null) {
			return;
		}

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getConfig();

		if (worldConfig.stepMode.equals(StepMode.STEP)) {
			double stepHeight = worldConfig.stepHeight;

			if (player.isSneaking()) {
				double heightToSet = Math.min(stepHeight, worldConfig.sneakHeight);

				this.setStepHeight(player, heightToSet);
			} else if (player.isSprinting() || client.options.sprintKey.isPressed()) {
				double heightToSet = Math.max(stepHeight, worldConfig.sprintHeight);

				this.setStepHeight(player, heightToSet);
			} else {
				this.setStepHeight(player, stepHeight);
			}
		} else {
			this.setStepHeight(player, Constants.VANILLA_STEP_HEIGHT);
		}
	}

	private void setStepHeight(ClientPlayerEntity player, double height) {
		EntityAttributeInstance stepHeightAttribute = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
		stepHeightAttribute.setBaseValue(height);
	}
}
