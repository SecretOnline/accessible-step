package co.secretonline.accessiblestep.fabric.client;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.event.AccessibleStepEndTick;
import co.secretonline.accessiblestep.event.AccessibleStepKeyboardHandlers;
import co.secretonline.accessiblestep.event.AccessibleStepNetworkHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class AccessibleStepFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AccessibleStepCommon.init(FabricLoader.getInstance().getConfigDir());

		KeyBindingHelper.registerKeyBinding(AccessibleStepKeyboardHandlers.keyBinding);

		AccessibleStepKeyboardHandlers keyboardHandlers = new AccessibleStepKeyboardHandlers();
		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();
		AccessibleStepNetworkHandlers networkHandlers = new AccessibleStepNetworkHandlers();

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			keyboardHandlers.onEndTick(client);
			endTickListener.onEndTick(client);
		});

		ClientPlayConnectionEvents.JOIN
				.register((handler, sender, client) -> networkHandlers.onJoin(handler.getServerInfo(), client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> networkHandlers.onLeave(client));
	}
}
