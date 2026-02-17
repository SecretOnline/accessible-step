package co.secretonline.accessiblestep.config;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class AccessibleStepConfigReader {
	private final Path configPath;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public AccessibleStepConfigReader(Path configDir) {
		this.configPath = configDir.resolve(String.format("%s.json", AccessibleStepCommon.MOD_ID));
	}

	public AccessibleStepConfig readConfig() {
		if (!(Files.exists(this.configPath) && Files.isRegularFile(this.configPath))) {
			AccessibleStepCommon.LOGGER.info("Creating config file for {}.", AccessibleStepCommon.MOD_ID);

			AccessibleStepConfig config = AccessibleStepConfig.getDefault();

			writeConfig(config);
			return config;
		}

		try (InputStream input = Files.newInputStream(this.configPath)) {
			try (InputStreamReader reader = new InputStreamReader(new BufferedInputStream(input))) {
				JsonElement element = JsonParser.parseReader(reader);

				return AccessibleStepConfig.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow();
			}
		} catch (IOException | IllegalStateException err) {
			AccessibleStepCommon.LOGGER.error("Unable to read config file. Revering to default settings.");
			return AccessibleStepConfig.getDefault();
		}
	}

	public void writeConfig(AccessibleStepConfig config) {
		if (!(Files.exists(this.configPath) && Files.isRegularFile(this.configPath))) {
			try {
				Files.createFile(this.configPath);
			} catch (IOException e) {
				AccessibleStepCommon.LOGGER.error("Unable to create config file. Pending changes will be lost.");
				return;
			}
		}

		try {
			try (OutputStream output = Files.newOutputStream(configPath)) {
				try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(output))) {
					JsonElement element = AccessibleStepConfig.CODEC.encodeStart(JsonOps.INSTANCE, config).getOrThrow();

					String json = GSON.toJson(element);
					writer.append(json);
				}
			}
		} catch (IOException|IllegalStateException err) {
			AccessibleStepCommon.LOGGER.error("Unable to save config file. Pending changes will be lost.");
		}
	}
}
