package co.secretonline.accessiblestep.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.secretonline.accessiblestep.screen.AccessibleStepOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {
	@Inject(method = "options", at = @At("RETURN"), cancellable = true)
	private static void appendStepModeOption(Options gameOptions, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
		OptionInstance<?>[] original = cir.getReturnValue();
		OptionInstance<?>[] modified = new OptionInstance<?>[original.length + 1];
		System.arraycopy(original, 0, modified, 0, original.length);
		modified[original.length] = AccessibleStepOptions.getStepModeOption();
		cir.setReturnValue(modified);
	}
}
