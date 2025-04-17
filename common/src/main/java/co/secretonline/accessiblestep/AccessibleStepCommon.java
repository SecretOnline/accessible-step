package co.secretonline.accessiblestep;

import java.nio.file.Path;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.secretonline.accessiblestep.config.AccessibleStepConfigReader;
import co.secretonline.accessiblestep.event.StepHeightHandler;
import co.secretonline.accessiblestep.event.KeyboardHandler;
import co.secretonline.accessiblestep.event.NetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class AccessibleStepCommon {
	public static final String MOD_ID = "accessible-step";
	// (Neo)Forge doesn't support dashes in mod IDs, but it's a bit late now.
	public static final String FORGE_MOD_ID = "accessible_step";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static KeyBinding STEP_MODE_KEY_BINDING = new KeyBinding(
			"key.accessiblestep.mode",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"category.accessiblestep.title");

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	private static AccessibleStepCommon SINGLETON_INSTANCE = null;

	public static AccessibleStepCommon init(Path configDir) {
		if (SINGLETON_INSTANCE == null) {
			SINGLETON_INSTANCE = new AccessibleStepCommon(configDir);
		}

		return SINGLETON_INSTANCE;
	}

	private KeyboardHandler keyboardHandler;
	private StepHeightHandler stepHeightHandler;
	private NetworkHandler networkHandler;

	private AccessibleStepCommon(Path configDir) {
		State.configReader = new AccessibleStepConfigReader(configDir);
		State.config = State.configReader.readConfig();

		this.keyboardHandler = new KeyboardHandler();
		this.stepHeightHandler = new StepHeightHandler();
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
}
