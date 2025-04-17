package co.secretonline.accessiblestep.neoforge;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.mixin.ClientPlayerInteractionManagerAccessor;
import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.TickEvent.ClientTickEvent;

@Mod(AccessibleStepCommon.FORGE_MOD_ID)
public final class AccessibleStepNeoForge {
	public AccessibleStepNeoForge(IEventBus modBus) {
		ModContainer container = ModList.get().getModContainerById(AccessibleStepCommon.FORGE_MOD_ID).orElseThrow();

		AccessibleStepCommon common = AccessibleStepCommon.init(
				FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()),
				AccessibleStepNeoForge::setStepHeightNeoForgeAttribute);

		modBus.addListener(
				RegisterKeyMappingsEvent.class,
				(event) -> event.register(AccessibleStepCommon.STEP_MODE_KEY_BINDING));
		NeoForge.EVENT_BUS.addListener(
				ClientTickEvent.class,
				(event) -> {
					if (event.phase == TickEvent.Phase.END) {
						common.onEndTick(MinecraftClient.getInstance());
					}
				});
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingIn.class,
				(event) -> common.onJoin(this.getServerInfo(event), MinecraftClient.getInstance()));
		NeoForge.EVENT_BUS.addListener(
				ClientPlayerNetworkEvent.LoggingOut.class,
				(event) -> common.onLeave(MinecraftClient.getInstance()));

		container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new ConfigScreenHandler.ConfigScreenFactory(
					(minecraft, parent) -> new AccessibleStepOptionsScreen(parent, client.options));
		});
	}

	private ServerInfo getServerInfo(ClientPlayerNetworkEvent.LoggingIn event) {
		ClientPlayNetworkHandler handler = ((ClientPlayerInteractionManagerAccessor) (event.getMultiPlayerGameMode()))
				.getNetworkHandler();
		return handler.getServerInfo();
	}

	private static void setStepHeightNeoForgeAttribute(PlayerEntity player, double height) {
		// Forge added its own attribute for step height before the base game did.
		// The Fabric version of this mod still uses PlayerEntity#setStepHeight().
		EntityAttributeInstance stepHeightAttribute = player.getAttributeInstance(NeoForgeMod.STEP_HEIGHT.value());
		if (stepHeightAttribute != null) {
			// This attribute is a modifier to the base step height, so subtract the
			// default.
			stepHeightAttribute.setBaseValue(height - Constants.VANILLA_STEP_HEIGHT);
		}
	}
}
