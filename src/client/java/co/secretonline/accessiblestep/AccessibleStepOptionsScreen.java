package co.secretonline.accessiblestep;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptionsScreen extends SimpleOptionsScreen {
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[] { AccessibleStepOptions.getStepOption() };
	}

	public AccessibleStepOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(
				parent,
				gameOptions,
				Text.translatable("options.accessiblestep.title"),
				getOptions(gameOptions));
	}
}
