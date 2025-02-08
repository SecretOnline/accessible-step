package co.secretonline.accessiblestep.options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import co.secretonline.accessiblestep.AccessibleStepClient;
import co.secretonline.accessiblestep.options.migration.MigrateFromOptionsTxt;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class AccessibleStepConfigReader {
	public static final String CONFIG_PATH = String.format("%s/%s.json",
			FabricLoader.getInstance().getConfigDir().toString(), AccessibleStepClient.MOD_ID);

	public static AccessibleStepConfig readConfig(MinecraftClient client) {
		File configFile = new File(CONFIG_PATH);
		if (!configFile.exists()) {
			AccessibleStepClient.LOGGER.info(String.format("Creating config file for %s.", AccessibleStepClient.MOD_ID));

			AccessibleStepConfig config = MigrateFromOptionsTxt.readConfig(client);
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
			AccessibleStepClient.LOGGER.error(String.format("Unable to read config file. Revering to default settings."));
			return new AccessibleStepConfig();
		}
	}

	public static void writeConfig(AccessibleStepConfig config) {
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
		String result = jankson.toJson(config).toJson(true, true);

		try {
			FileOutputStream out = new FileOutputStream(configFile, false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (IOException err) {
			AccessibleStepClient.LOGGER.error(String.format("Unable to save config file. Pending changes will be lost."));
		}
	}
}
