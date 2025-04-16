package co.secretonline.accessiblestep;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.secretonline.accessiblestep.config.AccessibleStepConfigReader;
import net.minecraft.util.Identifier;

public class AccessibleStepCommon {
	public static final String MOD_ID = "accessible-step";
	// (Neo)Forge doesn't support dashes in mod IDs, but it's a bit late now.
	public static final String FORGE_MOD_ID = "accessible_step";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static void init(Path configDir) {
		State.configReader = new AccessibleStepConfigReader(configDir);
		State.config = State.configReader.readConfig();
	}
}
