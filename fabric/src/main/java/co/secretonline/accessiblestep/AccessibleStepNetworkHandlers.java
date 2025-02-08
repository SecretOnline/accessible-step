package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.WorldSavePath;

public class AccessibleStepNetworkHandlers implements Join, Disconnect {
	@Override
	public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
		ServerInfo serverInfo = handler.getServerInfo();

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

	@Override
	public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
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
