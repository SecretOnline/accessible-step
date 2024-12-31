package co.secretonline.accessiblestep.options;

import java.util.HashMap;
import java.util.Map;

import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import net.minecraft.client.MinecraftClient;

public class AccessibleStepConfig {
	public int version = 1;
	public WorldConfig defaultConfig = new WorldConfig();
	public Map<String, WorldConfig> worlds = new HashMap<>();

	public WorldConfig getCurrentWorldConfig() {
		String worldName = State.worldName;
		if (worldName == null) {
			return this.defaultConfig;
		}

		return this.worlds.getOrDefault(worldName, this.defaultConfig);
	}

	public boolean hasConfigForWorld() {
		String worldName = State.worldName;
		if (worldName == null) {
			return false;
		}

		return this.worlds.containsKey(worldName);
	}

	public void setHasConfigForWorld(boolean value) {
		boolean worldHasConfig = this.hasConfigForWorld();
		if ((value && worldHasConfig) || (!value && !worldHasConfig)) {
			return;
		}

		String worldName = State.worldName;
		if (worldName == null) {
			// Safeguard for if the toggle gets called at the wrong time.
			return;
		}
		if (value) {
			this.worlds.put(worldName, defaultConfig.copy());
		} else {
			this.worlds.remove(worldName);
		}

		AccessibleStepConfigReader.writeConfig(this);
	}

	public StepMode getStepMode() {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		return worldConfig.stepMode;
	}

	public void setStepMode(StepMode stepMode) {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		if (worldConfig.stepMode == stepMode) {
			return;
		}

		worldConfig.stepMode = stepMode;

		// Also update auto-jump option behind the scenes
		MinecraftClient client = MinecraftClient.getInstance();
		if (stepMode == StepMode.AUTO_JUMP) {
			client.options.getAutoJump().setValue(true);
		} else {
			client.options.getAutoJump().setValue(false);
		}

		AccessibleStepConfigReader.writeConfig(this);
	}

	public double getStepHeight() {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		return worldConfig.stepHeight;
	}

	public void setStepHeight(double stepHeight) {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		if (worldConfig.stepHeight == stepHeight) {
			return;
		}

		worldConfig.stepHeight = stepHeight;

		AccessibleStepConfigReader.writeConfig(this);
	}

	public double getSneakHeight() {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		return worldConfig.sneakHeight;
	}

	public void setSneakHeight(double sneakHeight) {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		if (worldConfig.sneakHeight == sneakHeight) {
			return;
		}

		worldConfig.sneakHeight = sneakHeight;

		AccessibleStepConfigReader.writeConfig(this);
	}

	public double getSprintHeight() {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		return worldConfig.sprintHeight;
	}

	public void setSprintHeight(double sprintHeight) {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		if (worldConfig.sprintHeight == sprintHeight) {
			return;
		}

		worldConfig.sprintHeight = sprintHeight;

		AccessibleStepConfigReader.writeConfig(this);
	}

	public boolean getFullRange() {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		return worldConfig.useFullRange;
	}

	public void setFullRange(boolean useFullRange) {
		WorldConfig worldConfig = this.getCurrentWorldConfig();
		if (worldConfig.useFullRange == useFullRange) {
			return;
		}

		worldConfig.useFullRange = useFullRange;

		AccessibleStepConfigReader.writeConfig(this);
	}

	public static class WorldConfig {
		public StepMode stepMode = StepMode.OFF;
		public double stepHeight = Constants.MOD_DEFAULT_STEP_HEIGHT;
		public double sneakHeight = Constants.MOD_DEFAULT_SNEAK_HEIGHT;
		public double sprintHeight = Constants.MOD_DEFAULT_SPRINT_HEIGHT;
		public boolean useFullRange = false;

		public WorldConfig copy() {
			WorldConfig newConfig = new WorldConfig();
			newConfig.stepMode = this.stepMode;
			newConfig.stepHeight = this.stepHeight;
			newConfig.sneakHeight = this.sneakHeight;
			newConfig.sprintHeight = this.sprintHeight;
			newConfig.useFullRange = this.useFullRange;

			return newConfig;
		}
	}
}
