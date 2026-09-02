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
	private final BiConsumer<Player, Double> setStepHeight;

	public StepHeightHandler(BiConsumer<Player, Double> setStepHeight) {
		this.setStepHeight = setStepHeight;
	}

	public void onEndTick(Minecraft client) {
		LocalPlayer player = client.player;

		if (player == null) {
			return;
		}

		AccessibleStepConfig.WorldConfig worldConfig = State.config.getCurrentWorldConfig();

		if (worldConfig.stepMode().equals(StepMode.STEP)) {
			if (player.isShiftKeyDown()) {
				this.setStepHeight.accept(player, worldConfig.sneakHeight());
			} else if ((player.isSprinting() || client.options.keySprint.isDown()) && client.player.input.hasForwardImpulse()) {
				this.setStepHeight.accept(player, worldConfig.sprintHeight());
			} else {
				this.setStepHeight.accept(player, worldConfig.stepHeight());
			}
		} else {
			this.setStepHeight.accept(player, Constants.VANILLA_STEP_HEIGHT);
		}
	}
}
