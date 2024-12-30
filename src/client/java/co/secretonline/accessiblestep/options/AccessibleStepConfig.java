package co.secretonline.accessiblestep.options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import co.secretonline.accessiblestep.AccessibleStepClient;
import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.State;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class AccessibleStepConfig {
	public static final String CONFIG_PATH = String.format("%s/%s.json",
			FabricLoader.getInstance().getConfigDir().toString(), AccessibleStepClient.MOD_ID);

	public WorldConfig defaultConfig = new WorldConfig();
	public Map<String, WorldConfig> worlds = new HashMap<>();
	public boolean useFullRange;

	public boolean hasConfigForWorld(@Nullable String worldName) {
		if (worldName == null) {
			return false;
		}

		return this.worlds.containsKey(worldName);
	}

	public WorldConfig getConfig() {
		String worldName = State.worldName;
		if (worldName == null) {
			return this.defaultConfig;
		}

		return this.worlds.getOrDefault(worldName, this.defaultConfig);
	}

	public void setWorldConfigMode(String worldName, boolean hasOwnConfig) {
		if ((hasOwnConfig && this.hasConfigForWorld(worldName)) || (!hasOwnConfig && !this.hasConfigForWorld(worldName))) {
			return;
		}

		if (hasOwnConfig) {
			this.worlds.put(worldName, new WorldConfig());
		} else {
			this.worlds.remove(worldName);
		}

		this.saveConfig();
	}

	public boolean getFullRange() {
		return this.useFullRange;
	}

	public void setFullRange(boolean useFullRange) {
		this.useFullRange = useFullRange;
		this.saveConfig();
	}

	public StepMode getStepMode() {
		WorldConfig worldConfig = this.getConfig();

		return worldConfig.stepMode;
	}

	public void setStepMode(StepMode stepMode) {
		// Also update auto-jump option behind the scenes
		MinecraftClient client = MinecraftClient.getInstance();
		if (stepMode == StepMode.AUTO_JUMP) {
			client.options.getAutoJump().setValue(true);
		} else {
			client.options.getAutoJump().setValue(false);
		}

		WorldConfig worldConfig = this.getConfig();
		worldConfig.setStepMode(stepMode);

		this.saveConfig();
	}

	public double getStepHeight() {
		WorldConfig worldConfig = this.getConfig();

		return worldConfig.stepHeight;
	}

	public void setStepHeight(double stepHeight) {
		WorldConfig worldConfig = this.getConfig();
		worldConfig.setStepHeight(stepHeight);

		this.saveConfig();
	}

	public double getSneakHeight() {
		WorldConfig worldConfig = this.getConfig();

		return worldConfig.sneakHeight;
	}

	public void setSneakHeight(double sneakHeight) {
		WorldConfig worldConfig = this.getConfig();
		worldConfig.setSneakHeight(sneakHeight);

		this.saveConfig();
	}

	public double getSprintHeight() {
		WorldConfig worldConfig = this.getConfig();

		return worldConfig.sprintHeight;
	}

	public void setSprintHeight(double sprintHeight) {
		WorldConfig worldConfig = this.getConfig();
		worldConfig.setSprintHeight(sprintHeight);

		this.saveConfig();
	}

	public static AccessibleStepConfig loadConfig() {
		File configFile = new File(CONFIG_PATH);
		if (!configFile.exists()) {
			AccessibleStepClient.LOGGER.info(String.format("Creating config file for %s.", AccessibleStepClient.MOD_ID));
			AccessibleStepConfig config = new AccessibleStepConfig();
			config.saveConfig();
			return config;
		}

		try {
			Jankson jankson = Jankson.builder().build();
			JsonObject configJson = jankson.load(configFile);
			return jankson.fromJson(configJson, AccessibleStepConfig.class);
		} catch (IOException | SyntaxError err) {
			AccessibleStepClient.LOGGER.error(String.format("Unable to read config file. Revering to default settings."));
			return new AccessibleStepConfig();
		}
	}

	public void saveConfig() {
		File configFile = new File(CONFIG_PATH);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				AccessibleStepClient.LOGGER.error(String.format("Unable to create config file. Pending changes will be lost."));
				return;
			}
		}

		Jankson jankson = Jankson.builder().build();
		String result = jankson.toJson(this).toJson(true, true);

		try {
			FileOutputStream out = new FileOutputStream(configFile, false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (IOException err) {
			AccessibleStepClient.LOGGER.error(String.format("Unable to save config file. Pending changes will be lost."));
		}
	}

	public static class WorldConfig {
		public StepMode stepMode = StepMode.OFF;
		public double stepHeight = Constants.MOD_DEFAULT_STEP_HEIGHT;
		public double sneakHeight = Constants.MOD_DEFAULT_SNEAK_HEIGHT;
		public double sprintHeight = Constants.MOD_DEFAULT_SPRINT_HEIGHT;

		public void setStepMode(StepMode stepMode) {
			this.stepMode = stepMode;
		}

		public void setStepHeight(double stepHeight) {
			this.stepHeight = stepHeight;
		}

		public void setSneakHeight(double sneakHeight) {
			this.sneakHeight = sneakHeight;
		}

		public void setSprintHeight(double sprintHeight) {
			this.sprintHeight = sprintHeight;
		}
	}
}
