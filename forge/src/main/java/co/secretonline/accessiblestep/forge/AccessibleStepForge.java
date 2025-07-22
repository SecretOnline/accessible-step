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
import net.minecraftforge.event.TickEvent;
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

	// Apotheosis uses the value of the ForgeMod.STEP_HEIGHT_ADDITION attribute as
	// the step height directly. If Apotheosis is present, then we need to have
	// different bahaviour. Gah.
	private static String APOTHEOSIS_MOD_ID = "apotheosis";
	private static String APOTHIC_ATTRIBUTES_MOD_ID = "attributeslib";
	boolean shouldUseZeroBaseStepHeight;

	public AccessibleStepForge() {
		ModContainer container = ModList.get().getModContainerById(AccessibleStepCommon.FORGE_MOD_ID).orElseThrow();
		// Suppressing this is OK as this mod doesn't even support Forge by the time
		// this is removed.
		@SuppressWarnings("removal")
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		this.common = AccessibleStepCommon.init(
				FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()),
				this::setStepHeightForgeAttribute);

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

		this.shouldUseZeroBaseStepHeight = ModList.get().getModContainerById(APOTHEOSIS_MOD_ID).isPresent()
			|| ModList.get().getModContainerById(APOTHIC_ATTRIBUTES_MOD_ID).isPresent();
	}

	private void onPostTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			this.common.onEndTick(MinecraftClient.getInstance());
		}
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

	private void setStepHeightForgeAttribute(PlayerEntity player, double height) {
		// Forge added its own attribute for step height before the base game did.
		// The Fabric version of this mod still uses PlayerEntity#setStepHeight().
		EntityAttributeInstance stepHeightAttribute = player.getAttributeInstance(ForgeMod.STEP_HEIGHT_ADDITION.get());
		if (stepHeightAttribute != null) {
			// Apotheosis uses ForgeMod.STEP_HEIGHT_ADDITION as if it was a plain step
			// height and not a modifier.
			double baseStepHeight = this.shouldUseZeroBaseStepHeight ? 0 : Constants.VANILLA_STEP_HEIGHT;

			stepHeightAttribute.setBaseValue(height - baseStepHeight);
		}
	}
}
