package co.secretonline.accessiblestep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

public final class AccessibleStep {
	public static final String MOD_ID = "accessible-step";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static void init() {
		// Write common init code here.
	}
}
