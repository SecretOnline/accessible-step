package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.mixin.client.ClientPlayerInteractionManagerAccessor;
import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AccessibleStep.MOD_ID, dist = Dist.CLIENT)
public final class AccessibleStepNeoForge {

	public AccessibleStepNeoForge(FMLModContainer container, IEventBus modBus, Dist dist) {
		AccessibleStep.init();

		AccessibleStepKeyboardHandlers keyboardHandlers = new AccessibleStepKeyboardHandlers();
		AccessibleStepEndTick endTickListener = new AccessibleStepEndTick();
		AccessibleStepNetworkHandlers networkHandlers = new AccessibleStepNetworkHandlers();

		modBus.addListener(RegisterKeyMappingsEvent.class,
				(event) -> event.register(AccessibleStepKeyboardHandlers.keyBinding));

		modBus.addListener(ClientTickEvent.Post.class, (event) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			keyboardHandlers.onEndTick(client);
			endTickListener.onEndTick(client);
		});

		modBus.addListener(ClientPlayerNetworkEvent.LoggingIn.class, (event) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			ClientPlayNetworkHandler handler = ((ClientPlayerInteractionManagerAccessor) (event.getMultiPlayerGameMode()))
					.getNetworkHandler();

			networkHandlers.onJoin(handler.getServerInfo(), client);
		});

		modBus.addListener(ClientPlayerNetworkEvent.LoggingOut.class, (event) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			networkHandlers.onLeave(client);
		});

		container.registerExtensionPoint(IConfigScreenFactory.class, (cont, parent) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		});
	}
}
