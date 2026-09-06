package co.secretonline.accessiblestep.event;

import java.util.function.BiConsumer;

import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import co.secretonline.accessiblestep.config.AccessibleStepConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class StepHeightHandler {
	private BiConsumer<PlayerEntity, Double> setStepHeight;

	public StepHeightHandler(BiConsumer<PlayerEntity, Double> setStepHeight) {
		this.setStepHeight = setStepHeight;
	}

	public void onEndTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;

		if (player == null) {
			return;
		}

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getCurrentWorldConfig();

		if (worldConfig.stepMode.equals(StepMode.STEP)) {
			if (player.isSneaking()) {
				this.setStepHeight.accept(player, worldConfig.sneakHeight);
			} else if ((player.isSprinting() || client.options.sprintKey.isPressed()) && client.player.input.hasForwardMovement()) {
				this.setStepHeight.accept(player, worldConfig.sprintHeight);
			} else {
				this.setStepHeight.accept(player, worldConfig.stepHeight);
			}
		} else {
			this.setStepHeight.accept(player, Constants.VANILLA_STEP_HEIGHT);
		}
	}
}
