package co.secretonline.accessiblestep.neoforge;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.AccessibleStepEndTick;
import co.secretonline.accessiblestep.AccessibleStepKeyboardHandlers;
import co.secretonline.accessiblestep.AccessibleStepNetworkHandlers;
import co.secretonline.accessiblestep.mixin.ClientPlayerInteractionManagerAccessor;
import co.secretonline.accessiblestep.modmenu.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
    private AccessibleStepKeyboardHandlers keyboardHandlers = null;
    private AccessibleStepEndTick endTickListener = null;
    private AccessibleStepNetworkHandlers networkHandlers = null;

    public AccessibleStepNeoForge(FMLModContainer container, IEventBus modBus, Dist dist) {
        AccessibleStepCommon.init(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()));

        keyboardHandlers = new AccessibleStepKeyboardHandlers();
        endTickListener = new AccessibleStepEndTick();
        networkHandlers = new AccessibleStepNetworkHandlers();

        modBus.addListener(this::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onClientTickPost);
        NeoForge.EVENT_BUS.addListener(this::onClientPlayerNetworkLoggingIn);
        NeoForge.EVENT_BUS.addListener(this::onClientPlayerNetworkLoggingOut);

        container.registerExtensionPoint(IConfigScreenFactory.class, (cont, parent) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            return new AccessibleStepOptionsScreen(parent, client.options);
        });
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(AccessibleStepKeyboardHandlers.keyBinding);
    }

    private void onClientTickPost(ClientTickEvent.Post event) {
        MinecraftClient client = MinecraftClient.getInstance();

        this.keyboardHandlers.onEndTick(client);
        this.endTickListener.onEndTick(client);
    }

    private void onClientPlayerNetworkLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientPlayNetworkHandler handler = ((ClientPlayerInteractionManagerAccessor) (event.getMultiPlayerGameMode()))
                .getNetworkHandler();

        this.networkHandlers.onJoin(handler.getServerInfo(), client);
    }

    private void onClientPlayerNetworkLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MinecraftClient client = MinecraftClient.getInstance();

        this.networkHandlers.onLeave(client);
    }
}
