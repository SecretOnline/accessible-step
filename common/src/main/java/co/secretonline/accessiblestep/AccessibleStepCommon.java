package co.secretonline.accessiblestep;

import java.nio.file.Path;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.secretonline.accessiblestep.config.AccessibleStepConfigReader;
import co.secretonline.accessiblestep.event.KeyboardHandler;
import co.secretonline.accessiblestep.event.NetworkHandler;
import co.secretonline.accessiblestep.event.StepHeightHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class AccessibleStepCommon {
	public static final String MOD_ID = "accessible-step";
	// (Neo)Forge doesn't support dashes in mod IDs, but it's a bit late now.
	public static final String FORGE_MOD_ID = "accessible_step";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final KeyBinding.Category ACCESSIBLE_STEP_KEYBINDING_CATEGORY = KeyBinding.Category
			.create(AccessibleStepCommon.id("title"));

	public static KeyBinding STEP_MODE_KEY_BINDING = new KeyBinding(
			"key.accessiblestep.mode",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			ACCESSIBLE_STEP_KEYBINDING_CATEGORY);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	private static AccessibleStepCommon SINGLETON_INSTANCE = null;

	public static AccessibleStepCommon init(Path configDir, BiConsumer<PlayerEntity, Double> setStepHeight) {
		if (SINGLETON_INSTANCE == null) {
			SINGLETON_INSTANCE = new AccessibleStepCommon(configDir, setStepHeight);
		}

		return SINGLETON_INSTANCE;
	}

	private KeyboardHandler keyboardHandler;
	private StepHeightHandler stepHeightHandler;
	private NetworkHandler networkHandler;

	private AccessibleStepCommon(Path configDir, BiConsumer<PlayerEntity, Double> setStepHeight) {
		State.configReader = new AccessibleStepConfigReader(configDir);
		State.config = State.configReader.readConfig();

		this.keyboardHandler = new KeyboardHandler();
		this.stepHeightHandler = new StepHeightHandler(setStepHeight);
		this.networkHandler = new NetworkHandler();
	}

	public void onEndTick(MinecraftClient client) {
		this.keyboardHandler.onEndTick(client);
		this.stepHeightHandler.onEndTick(client);
	}

	public void onJoin(ServerInfo serverInfo, MinecraftClient client) {
		this.networkHandler.onJoin(serverInfo, client);
	}

	public void onLeave(MinecraftClient client) {
		this.networkHandler.onLeave(client);
	}

	public static void setStepHeightAttribute(PlayerEntity player, double height) {
		EntityAttributeInstance stepHeightAttribute = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
		stepHeightAttribute.setBaseValue(height);
	}
}
