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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributes;

public class AccessibleStepConfig {
	public static final String CONFIG_PATH = String.format("%s/%s.json",
			FabricLoader.getInstance().getConfigDir().toString(), AccessibleStepClient.MOD_ID);

	public static final double VANILLA_STEP_HEIGHT = EntityAttributes.STEP_HEIGHT.value().getDefaultValue();
	private static final double MOD_DEFAULT_STEP_HEIGHT = 1.25;
	private static final double MOD_DEFAULT_SNEAK_HEIGHT = VANILLA_STEP_HEIGHT;
	private static final double MOD_DEFAULT_SPRINT_HEIGHT = MOD_DEFAULT_STEP_HEIGHT;

	public boolean useFullRange;
	public WorldConfig defaultConfig = new WorldConfig(
			StepMode.OFF,
			MOD_DEFAULT_STEP_HEIGHT,
			MOD_DEFAULT_SNEAK_HEIGHT,
			MOD_DEFAULT_SPRINT_HEIGHT);
	public Map<String, WorldConfig> worlds = new HashMap<>();

	public boolean hasConfigForWorld(@Nullable String worldName) {
		if (worldName == null) {
			return false;
		}

		return this.worlds.containsKey(worldName);
	}

	public WorldConfig getConfigForWorld(String worldName) {
		return this.worlds.getOrDefault(worldName, this.defaultConfig);
	}

	public void setWorldHasConfig(String worldName, boolean enabled) {
		if ((enabled && this.hasConfigForWorld(worldName)) || (!enabled && !this.hasConfigForWorld(worldName))) {
			return;
		}

		if (enabled) {
			this.worlds.put(worldName, new WorldConfig());
		} else {
			this.worlds.remove(worldName);
		}

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
		public double stepHeight = MOD_DEFAULT_STEP_HEIGHT;
		public double sneakHeight = MOD_DEFAULT_SNEAK_HEIGHT;
		public double sprintHeight = MOD_DEFAULT_SPRINT_HEIGHT;

		public WorldConfig() {
		}

		public WorldConfig(StepMode stepMode, double stepHeight, double sneakHeight, double sprintHeight) {
			this.stepMode = stepMode;
			this.stepHeight = stepHeight;
			this.sneakHeight = sneakHeight;
			this.sprintHeight = sprintHeight;
		}

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
