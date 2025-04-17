package co.secretonline.accessiblestep;

import java.nio.file.Path;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.InputConstants;

import co.secretonline.accessiblestep.config.AccessibleStepConfigReader;
import co.secretonline.accessiblestep.event.KeyboardHandler;
import co.secretonline.accessiblestep.event.NetworkHandler;
import co.secretonline.accessiblestep.event.StepHeightHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class AccessibleStepCommon {
	public static final String MOD_ID = "accessible-step";
	// (Neo)Forge doesn't support dashes in mod IDs, but it's a bit late now.
	public static final String FORGE_MOD_ID = "accessible_step";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static KeyMapping STEP_MODE_KEY_BINDING = new KeyMapping(
			"key.accessiblestep.mode",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"key.category.accessible-step.title");

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	private static AccessibleStepCommon SINGLETON_INSTANCE = null;

	public static AccessibleStepCommon init(Path configDir, BiConsumer<Player, Double> setStepHeight) {
		if (SINGLETON_INSTANCE == null) {
			SINGLETON_INSTANCE = new AccessibleStepCommon(configDir, setStepHeight);
		}

		return SINGLETON_INSTANCE;
	}

	private KeyboardHandler keyboardHandler;
	private StepHeightHandler stepHeightHandler;
	private NetworkHandler networkHandler;

	private AccessibleStepCommon(Path configDir, BiConsumer<Player, Double> setStepHeight) {
		State.configReader = new AccessibleStepConfigReader(configDir);
		State.config = State.configReader.readConfig();

		this.keyboardHandler = new KeyboardHandler();
		this.stepHeightHandler = new StepHeightHandler(setStepHeight);
		this.networkHandler = new NetworkHandler();
	}

	public void onEndTick(Minecraft client) {
		this.keyboardHandler.onEndTick(client);
		this.stepHeightHandler.onEndTick(client);
	}

	public void onJoin(ServerData serverInfo, Minecraft client) {
		this.networkHandler.onJoin(serverInfo, client);
	}

	public void onLeave(Minecraft client) {
		this.networkHandler.onLeave(client);
	}

	public static void setStepHeightAttribute(Player player, double height) {
		AttributeInstance stepHeightAttribute = player.getAttribute(Attributes.STEP_HEIGHT);
		stepHeightAttribute.setBaseValue(height);
	}
}
