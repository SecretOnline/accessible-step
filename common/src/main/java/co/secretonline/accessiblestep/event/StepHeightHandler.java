package co.secretonline.accessiblestep.event;

import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import co.secretonline.accessiblestep.config.AccessibleStepConfig;

public class StepHeightHandler {
	private BiConsumer<Player, Double> setStepHeight;

	public StepHeightHandler(BiConsumer<Player, Double> setStepHeight) {
		this.setStepHeight = setStepHeight;
	}

	public void onEndTick(Minecraft client) {
		LocalPlayer player = client.player;

		if (player == null) {
			return;
		}

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getCurrentWorldConfig();

		if (worldConfig.stepMode.equals(StepMode.STEP)) {
			double stepHeight = worldConfig.stepHeight;

			if (player.isShiftKeyDown()) {
				double heightToSet = Math.min(stepHeight, worldConfig.sneakHeight);

				this.setStepHeight.accept(player, heightToSet);
			} else if (player.isSprinting() || client.options.keySprint.isDown()) {
				double heightToSet = Math.max(stepHeight, worldConfig.sprintHeight);

				this.setStepHeight.accept(player, heightToSet);
			} else {
				this.setStepHeight.accept(player, stepHeight);
			}
		} else {
			this.setStepHeight.accept(player, Constants.VANILLA_STEP_HEIGHT);
		}
	}
}
