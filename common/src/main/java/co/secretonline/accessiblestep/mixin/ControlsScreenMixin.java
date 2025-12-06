package co.secretonline.accessiblestep.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import co.secretonline.accessiblestep.screen.AccessibleStepOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;

@Mixin(ControlsScreen.class)
public class ControlsScreenMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;autoJump()Lnet/minecraft/client/OptionInstance;"), method = "options")
	private static OptionInstance<?> replaceAutoJump(Options gameOptions) {
		return AccessibleStepOptions.getStepModeOption();
	}
}
