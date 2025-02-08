package co.secretonline.accessiblestep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Identifier;

public class AccessibleStepFabric implements ClientModInitializer {
	public static final String MOD_ID = "accessible-step";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		AccessibleStep.init();

		KeyBindingHelper.registerKeyBinding(AccessibleStepKeyboardHandlers.keyBinding);

		AccessibleStepKeyboardHandlers keyboardHandlers = new AccessibleStepKeyboardHandlers();
		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			keyboardHandlers.onEndTick(client);
			endTickListener.onEndTick(client);
		});

		AccessibleStepNetworkHandlers networkHandlers = new AccessibleStepNetworkHandlers();
		ClientPlayConnectionEvents.JOIN
				.register((handler, sender, client) -> networkHandlers.onJoin(handler.getServerInfo(), client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> networkHandlers.onLeave(client));
	}
}
