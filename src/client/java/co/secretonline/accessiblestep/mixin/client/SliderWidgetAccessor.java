package co.secretonline.accessiblestep.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.widget.SliderWidget;

@Mixin(SliderWidget.class)
public interface SliderWidgetAccessor {
	@Invoker("setValue")
	public void invokeSetValue(double value);
}
