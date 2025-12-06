package co.secretonline.accessiblestep.event;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.level.storage.LevelResource;

public class NetworkHandler {
	public void onJoin(ServerData serverInfo, Minecraft client) {
		String type = "";
		String name = "";
		if (serverInfo == null) {
			type = "world";
			name = client.getSingleplayerServer().getWorldPath(LevelResource.ROOT).getParent().getFileName().toString();
		} else if (serverInfo.isLan()) {
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

	public void onLeave(Minecraft client) {
		State.worldName = null;

		this.updateStepMode(client);
	}

	private void updateStepMode(Minecraft client) {
		StepMode stepMode = State.config.getStepMode();
		if (stepMode == StepMode.AUTO_JUMP) {
			client.options.autoJump().set(true);
		} else {
			client.options.autoJump().set(false);
		}
	}
}
