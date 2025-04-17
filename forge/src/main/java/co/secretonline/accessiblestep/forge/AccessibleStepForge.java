package co.secretonline.accessiblestep.forge;

import co.secretonline.accessiblestep.AccessibleStepCommon;
import co.secretonline.accessiblestep.Constants;
import co.secretonline.accessiblestep.mixin.ClientPlayerInteractionManagerAccessor;
import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(AccessibleStepCommon.FORGE_MOD_ID)
public final class AccessibleStepForge {
	AccessibleStepCommon common;

	public AccessibleStepForge() {
		ModContainer container = ModList.get().getModContainerById(AccessibleStepCommon.FORGE_MOD_ID).orElseThrow();
		// Suppressing this is OK as this mod doesn't even support Forge by the time
		// this is removed.
		@SuppressWarnings("removal")
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		this.common = AccessibleStepCommon.init(
				FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()),
				AccessibleStepForge::setStepHeightForgeAttribute);

		modBus.addListener(
				(RegisterKeyMappingsEvent event) -> event.register(AccessibleStepCommon.STEP_MODE_KEY_BINDING));
		MinecraftForge.EVENT_BUS.addListener(this::onPostTick);
		MinecraftForge.EVENT_BUS.addListener(this::onLoggingIn);
		MinecraftForge.EVENT_BUS.addListener(this::onLoggingOut);

		container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new ConfigScreenHandler.ConfigScreenFactory(
					(minecraft, parent) -> new AccessibleStepOptionsScreen(parent, client.options));
		});
	}

	private void onPostTick(ClientTickEvent.Post event) {
		this.common.onEndTick(MinecraftClient.getInstance());
	}

	private void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		this.common.onJoin(this.getServerInfo(event), MinecraftClient.getInstance());
	}

	private void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		this.common.onLeave(MinecraftClient.getInstance());
	}

	private ServerInfo getServerInfo(ClientPlayerNetworkEvent.LoggingIn event) {
		ClientPlayNetworkHandler handler = ((ClientPlayerInteractionManagerAccessor) (event.getMultiPlayerGameMode()))
				.getNetworkHandler();
		return handler.getServerInfo();
	}

	private static void setStepHeightForgeAttribute(PlayerEntity player, double height) {
		// Forge added its own attribute for step height before the base game did.
		// The Fabric version of this mod still uses PlayerEntity#setStepHeight().
		EntityAttributeInstance stepHeightAttribute = player.getAttributeInstance(ForgeMod.STEP_HEIGHT_ADDITION.get());
		if (stepHeightAttribute != null) {
			// This attribute is a modifier to the base step height, so subtract the
			// default.
			stepHeightAttribute.setBaseValue(height - Constants.VANILLA_STEP_HEIGHT);
		}
	}
}
