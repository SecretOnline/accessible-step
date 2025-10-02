package co.secretonline.accessiblestep.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.secretonline.accessiblestep.screen.AccessibleStepOptions;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {
	@Inject(method = "getOptions", at = @At("RETURN"), cancellable = true)
	private static void appendStepModeOption(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir) {
		SimpleOption<?>[] original = cir.getReturnValue();
		SimpleOption<?>[] modified = new SimpleOption<?>[original.length + 1];
		System.arraycopy(original, 0, modified, 0, original.length);
		modified[original.length] = AccessibleStepOptions.getStepModeOption();
		cir.setReturnValue(modified);
	}
}
