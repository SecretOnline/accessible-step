package co.secretonline.accessiblestep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.secretonline.accessiblestep.options.AccessibleStepConfigReader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class AccessibleStepClient implements ClientModInitializer {
	public static final String MOD_ID = "accessible-step";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		MinecraftClient client = MinecraftClient.getInstance();
		State.config = AccessibleStepConfigReader.readConfig(client);

		AccessibleStepKeyboardHandlers keyboardHandlers = new AccessibleStepKeyboardHandlers();
		ClientTickEvents.END_CLIENT_TICK.register(keyboardHandlers);

		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();
		ClientTickEvents.END_CLIENT_TICK.register(endTickListener);

		AccessibleStepNetworkHandlers networkHandlers = new AccessibleStepNetworkHandlers();
		ClientPlayConnectionEvents.JOIN.register(networkHandlers);
		ClientPlayConnectionEvents.DISCONNECT.register(networkHandlers);
	}
}
