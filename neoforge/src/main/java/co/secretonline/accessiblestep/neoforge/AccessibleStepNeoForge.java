package co.secretonline.accessiblestep.neoforge;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.mixin.MultiPlayerGameModeAccessor;
import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AccessibleStepCommon.FORGE_MOD_ID, dist = Dist.CLIENT)
public final class AccessibleStepNeoForge {
	public AccessibleStepNeoForge(FMLModContainer container, IEventBus modBus, Dist dist) {
		AccessibleStepCommon common = AccessibleStepCommon.init(
				FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()),
				AccessibleStepCommon::setStepHeightAttribute);

		modBus.addListener(
				RegisterKeyMappingsEvent.class,
				(event) -> event.register(AccessibleStepCommon.STEP_MODE_KEY_BINDING));
		NeoForge.EVENT_BUS.addListener(
				ClientTickEvent.Post.class,
				(event) -> common.onEndTick(Minecraft.getInstance()));
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingIn.class,
				(event) -> common.onJoin(this.getServerInfo(event), Minecraft.getInstance()));
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingOut.class,
				(event) -> common.onLeave(Minecraft.getInstance()));

		container.registerExtensionPoint(IConfigScreenFactory.class, (cont, parent) -> {
			Minecraft client = Minecraft.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		});
	}

	private ServerData getServerInfo(ClientPlayerNetworkEvent.LoggingIn event) {
		ClientPacketListener handler = ((MultiPlayerGameModeAccessor) (event.getMultiPlayerGameMode()))
				.getConnection();
		return handler.getServerData();
	}
}
