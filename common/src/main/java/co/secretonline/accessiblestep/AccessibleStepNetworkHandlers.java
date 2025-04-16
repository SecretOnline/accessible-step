package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.options.StepMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.WorldSavePath;

public class AccessibleStepNetworkHandlers {
	public void onJoin(ServerInfo serverInfo, MinecraftClient client) {
		String type = "";
		String name = "";
		if (serverInfo == null) {
			type = "world";
			name = client.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
		} else if (serverInfo.isLocal()) {
			type = "lan";
			name = serverInfo.name;
		} else if (serverInfo.isRealm()) {
			type = "realm";
			name = serverInfo.name;
		} else {
			type = "server";
			name = serverInfo.name;
		}
		State.worldName = new StringBuilder().append(type).append(":").append(name).toString();

		this.updateStepMode(client);
	}

	public void onLeave(MinecraftClient client) {
		State.worldName = null;

		this.updateStepMode(client);
	}

	private void updateStepMode(MinecraftClient client) {
		StepMode stepMode = State.config.getStepMode();
		if (stepMode == StepMode.AUTO_JUMP) {
			client.options.getAutoJump().setValue(true);
		} else {
			client.options.getAutoJump().setValue(false);
		}
	}
}
