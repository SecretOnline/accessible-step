package co.secretonline.accessiblestep.options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.options.migration.MigrateFromOptionsTxt;

public class AccessibleStepConfigReader {
	private final String configPath;
	// This is only for the migration from options.txt and shoul be removed as soon
	// as possible. Using the parent directory (as in the constructor) isn't
	// necessarially reliable, but I don't think there'll be any cases where this is
	// true.
	private final Path gameDir;

	public AccessibleStepConfigReader(Path configDir) {
		this.configPath = String.format("%s/%s.json", configDir, AccessibleStepCommon.MOD_ID);
		this.gameDir = configDir.getParent();
	}

	public AccessibleStepConfig readConfig() {
		File configFile = new File(this.configPath);
		if (!configFile.exists()) {
			AccessibleStepCommon.LOGGER.info(String.format("Creating config file for %s.", AccessibleStepCommon.MOD_ID));

			AccessibleStepConfig config = MigrateFromOptionsTxt.readConfig(this.gameDir);
			if (config == null) {
				config = new AccessibleStepConfig();
			}

			writeConfig(config);

			return config;
		}

		try {
			Jankson jankson = Jankson.builder().build();
			JsonObject configJson = jankson.load(configFile);
			return jankson.fromJson(configJson, AccessibleStepConfig.class);
		} catch (IOException | SyntaxError err) {
			AccessibleStepCommon.LOGGER.error(String.format("Unable to read config file. Revering to default settings."));
			return new AccessibleStepConfig();
		}
	}

	public void writeConfig(AccessibleStepConfig config) {
		File configFile = new File(this.configPath);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				AccessibleStepCommon.LOGGER.error(String.format("Unable to create config file. Pending changes will be lost."));
				return;
			}
		}

		Jankson jankson = Jankson.builder().build();
		String result = jankson.toJson(config).toJson(true, true);

		try {
			FileOutputStream out = new FileOutputStream(configFile, false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (IOException err) {
			AccessibleStepCommon.LOGGER.error(String.format("Unable to save config file. Pending changes will be lost."));
		}
	}
}
