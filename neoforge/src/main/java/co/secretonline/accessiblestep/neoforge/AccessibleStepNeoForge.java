package co.secretonline.accessiblestep.neoforge;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.mixin.ClientPlayerInteractionManagerAccessor;
import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
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
				(event) -> common.onEndTick(MinecraftClient.getInstance()));
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingIn.class,
				(event) -> common.onJoin(this.getServerInfo(event), MinecraftClient.getInstance()));
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingOut.class,
				(event) -> common.onLeave(MinecraftClient.getInstance()));

		container.registerExtensionPoint(IConfigScreenFactory.class, (cont, parent) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		});
	}

	private ServerInfo getServerInfo(ClientPlayerNetworkEvent.LoggingIn event) {
		ClientPlayNetworkHandler handler = ((ClientPlayerInteractionManagerAccessor) (event.getMultiPlayerGameMode()))
				.getNetworkHandler();
		return handler.getServerInfo();
	}
}
