package co.secretonline.accessiblestep.fabric.client;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class AccessibleStepFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AccessibleStepCommon common = AccessibleStepCommon.init(
				FabricLoader.getInstance().getConfigDir(),
				AccessibleStepCommon::setStepHeightAttribute);

		KeyBindingHelper.registerKeyBinding(AccessibleStepCommon.STEP_MODE_KEY_BINDING);

		ClientTickEvents.END_CLIENT_TICK.register((client) -> common.onEndTick(client));
		ClientPlayConnectionEvents.JOIN
				.register((handler, sender, client) -> common.onJoin(handler.getServerInfo(), client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> common.onLeave(client));
	}
}
