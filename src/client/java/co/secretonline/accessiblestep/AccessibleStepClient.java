package co.secretonline.accessiblestep;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AccessibleStepClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();

		ClientTickEvents.END_CLIENT_TICK.register(endTickListener);
	}
}
