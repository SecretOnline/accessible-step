package co.secretonline.accessiblestep.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.MinecraftClient;

public class AccessibleStepModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		};
	}
}
