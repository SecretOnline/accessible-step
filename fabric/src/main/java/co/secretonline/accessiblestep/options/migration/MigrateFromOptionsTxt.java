package co.secretonline.accessiblestep.options.migration;

import java.io.BufferedReader;
import java.io.File;
import java.util.Iterator;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import co.secretonline.accessiblestep.AccessibleStepClient;
import co.secretonline.accessiblestep.options.AccessibleStepConfig;
import co.secretonline.accessiblestep.options.AccessibleStepConfig.WorldConfig;
import co.secretonline.accessiblestep.options.StepMode;
import net.minecraft.client.MinecraftClient;

/**
 * Reads the config from the old options.txt file.
 *
 * This class can be removed once all currently supported versions are no longer
 * supported. In practice this means after any 1.21 minor versions have been
 * dropped.
 */
public class MigrateFromOptionsTxt {
	private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);

	private static WorldConfig defaultValues = new WorldConfig();

	public String accessibleStep = defaultValues.stepMode.asString();
	public boolean accessibleStepFullRange = defaultValues.useFullRange;
	public double accessibleStepHeight = defaultValues.stepHeight;
	public double accessibleSneakHeight = defaultValues.sneakHeight;
	public double accessibleSprintHeight = defaultValues.sprintHeight;

	@Nullable
	public static AccessibleStepConfig readConfig(MinecraftClient client) {
		File optionsTxt = new File(client.runDirectory, "options.txt");

		try {
			if (!optionsTxt.exists()) {
				return null;
			}

			Jankson jankson = Jankson.builder().build();
			JsonObject options = new JsonObject();

			try (BufferedReader bufferedReader = Files.newReader(optionsTxt, Charsets.UTF_8)) {
				bufferedReader.lines().forEach((line) -> {
					try {
						Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();

						String key = iterator.next();
						String value = iterator.next();
						if (value.isEmpty()) {
							return;
						}

						options.put(key, jankson.loadElement(value));
					} catch (Exception err) {
						AccessibleStepClient.LOGGER.warn("Migration: Skipping bad option: {}", line);
					}
				});
			} catch (Throwable err) {
				AccessibleStepClient.LOGGER.warn("Migration: Stopped while parsing options.txt", err);
			}

			if (!options.containsKey("accessibleStep")) {
				return null;
			}

			MigrateFromOptionsTxt parsedOptions = jankson.fromJson(options, MigrateFromOptionsTxt.class);

			AccessibleStepConfig config = new AccessibleStepConfig();
			config.defaultConfig.stepMode = StepMode.bySerialisedId(parsedOptions.accessibleStep);
			config.defaultConfig.useFullRange = parsedOptions.accessibleStepFullRange;
			config.defaultConfig.stepHeight = parsedOptions.accessibleStepHeight;
			config.defaultConfig.sneakHeight = parsedOptions.accessibleSneakHeight;
			config.defaultConfig.sprintHeight = parsedOptions.accessibleSprintHeight;

			return config;
		} catch (Exception err) {
			AccessibleStepClient.LOGGER.error("Migration: Failed to migrate options from options.txt, will reset to default",
					err);
			return null;
		}
	}
}
