package co.secretonline.accessiblestep.config;

import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public record AccessibleStepConfig(int version, WorldConfig defaultConfig, Map<String, WorldConfig> worlds) {
	public static AccessibleStepConfig getDefault() {
		return new AccessibleStepConfig(1, WorldConfig.getDefault(), new HashMap<>());
	}

	public WorldConfig getCurrentWorldConfig() {
		String worldName = State.worldName;
		if (worldName == null) {
			return this.defaultConfig;
		}

		return this.worlds.getOrDefault(worldName, this.defaultConfig);
	}

	public void setCurrentWorldConfig(WorldConfig worldConfig) {
		String worldName = State.worldName;
		if (worldName == null) {
			return;
		}

		this.worlds.put(worldName, worldConfig);
		State.configReader.writeConfig(this);
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

		State.configReader.writeConfig(this);
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

		WorldConfig newWorldConfig = new WorldConfig(stepMode, worldConfig.stepHeight, worldConfig.sneakHeight, worldConfig.sprintHeight, worldConfig.useFullRange);
		this.setCurrentWorldConfig(newWorldConfig);

		// Also update auto-jump option behind the scenes
		Minecraft client = Minecraft.getInstance();
		if (stepMode == StepMode.AUTO_JUMP) {
			client.options.autoJump().set(true);
		} else {
			client.options.autoJump().set(false);
		}
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

		WorldConfig newWorldConfig = new WorldConfig(worldConfig.stepMode, stepHeight, worldConfig.sneakHeight, worldConfig.sprintHeight, worldConfig.useFullRange);
		this.setCurrentWorldConfig(newWorldConfig);
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

		WorldConfig newWorldConfig = new WorldConfig(worldConfig.stepMode, worldConfig.stepHeight, sneakHeight, worldConfig.sprintHeight, worldConfig.useFullRange);
		this.setCurrentWorldConfig(newWorldConfig);
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

		WorldConfig newWorldConfig = new WorldConfig(worldConfig.stepMode, worldConfig.stepHeight, worldConfig.sneakHeight, sprintHeight, worldConfig.useFullRange);
		this.setCurrentWorldConfig(newWorldConfig);
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

		WorldConfig newWorldConfig = new WorldConfig(worldConfig.stepMode, worldConfig.stepHeight, worldConfig.sneakHeight, worldConfig.sprintHeight, useFullRange);
		this.setCurrentWorldConfig(newWorldConfig);
	}

	public static Codec<AccessibleStepConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("version").forGetter(AccessibleStepConfig::version),
			WorldConfig.CODEC.fieldOf("defaultConfig").forGetter(AccessibleStepConfig::defaultConfig),
			Codec.unboundedMap(Codec.STRING, WorldConfig.CODEC).fieldOf("worlds").forGetter(AccessibleStepConfig::worlds))
		.apply(instance, AccessibleStepConfig::new));

	public record WorldConfig(StepMode stepMode, double stepHeight, double sneakHeight, double sprintHeight,
														boolean useFullRange) {
		public static WorldConfig getDefault() {
		return new WorldConfig(StepMode.OFF, Constants.MOD_DEFAULT_STEP_HEIGHT, Constants.MOD_DEFAULT_SNEAK_HEIGHT, Constants.MOD_DEFAULT_SPRINT_HEIGHT, false);
	}

		public WorldConfig copy() {
			return new WorldConfig(this.stepMode, this.stepHeight, this.sneakHeight, this.sprintHeight, this.useFullRange);
		}

		public static Codec<WorldConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				StepMode.CODEC.fieldOf("stepMode").forGetter(WorldConfig::stepMode),
				Codec.DOUBLE.fieldOf("stepHeight").forGetter(WorldConfig::stepHeight),
				Codec.DOUBLE.fieldOf("sneakHeight").forGetter(WorldConfig::sneakHeight),
				Codec.DOUBLE.fieldOf("sprintHeight").forGetter(WorldConfig::sprintHeight),
				Codec.BOOL.optionalFieldOf("useFullRange", false).forGetter(WorldConfig::useFullRange))
			.apply(instance, WorldConfig::new));
	}
}
