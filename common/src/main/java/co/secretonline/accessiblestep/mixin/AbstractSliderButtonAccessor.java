package co.secretonline.accessiblestep.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.components.AbstractSliderButton;

@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
	@Invoker("setValue")
	public void invokeSetValue(double value);
}
