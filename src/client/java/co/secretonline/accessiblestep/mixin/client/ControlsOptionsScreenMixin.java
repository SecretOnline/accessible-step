package co.secretonline.accessiblestep.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Mixin(ControlsOptionsScreen.class)
public class ControlsOptionsScreenMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAutoJump()Lnet/minecraft/client/option/SimpleOption;"), method = "init")
	private SimpleOption<?> replaceAutoJump(GameOptions gameOptions) {
		return AccessibleStepOptions.getStepModeOption();
	}
}
