package co.secretonline.accessiblestep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Identifier;

public class AccessibleStepClient implements ClientModInitializer {
	public static final String MOD_ID = "accessible-step";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();

		ClientTickEvents.END_CLIENT_TICK.register(endTickListener);
	}
}
