package co.secretonline.accessiblestep.modmenu;

import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AccessibleStepOptionsScreen extends GameOptionsScreen {
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[] { AccessibleStepOptions.getStepModeOption() };
	}

	public AccessibleStepOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(
				parent,
				gameOptions,
				Text.translatable("options.accessiblestep.title"));
	}

	@Override
	protected void addOptions() {
		this.body.addAll(getOptions(this.gameOptions));
	}
}
