package co.secretonline.accessiblestep.fabric.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import co.secretonline.accessiblestep.screen.AccessibleStepOptionsScreen;
import net.minecraft.client.Minecraft;;;

public class ModMenuFactory implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> {
			Minecraft client = Minecraft.getInstance();

			return new AccessibleStepOptionsScreen(parent, client.options);
		};
	}
}
