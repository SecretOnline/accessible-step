package co.secretonline.accessiblestep.fabric.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import co.secretonline.accessiblestep.modmenu.AccessibleStepOptionsScreen;
import net.minecraft.client.MinecraftClient;

public class ModMenuFactory implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		};
	}
}
