package co.secretonline.accessiblestep.fabric.client;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class AccessibleStepFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AccessibleStepCommon common = AccessibleStepCommon.init(
				FabricLoader.getInstance().getConfigDir(),
				AccessibleStepCommon::setStepHeightAttribute);

		KeyMappingHelper.registerKeyMapping(AccessibleStepCommon.STEP_MODE_KEY_MAPPING);

		ClientTickEvents.END_CLIENT_TICK.register(common::onEndTick);
		ClientPlayConnectionEvents.JOIN
				.register((handler, sender, client) -> common.onJoin(handler.getServerData(), client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> common.onLeave(client));
	}
}
