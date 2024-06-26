package co.secretonline.accessiblestep;

import co.secretonline.accessiblestep.options.AccessibleStepOptions;
import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AccessibleStepEndTick implements EndTick {
	private static final Identifier STEP_HEIGHT_ATTRIBUTE_ID = Identifier.ofVanilla("generic.step_height");

	private static final RegistryEntry<EntityAttribute> STEP_HEIGHT_ATTRIBUTE = Registries.ATTRIBUTE
			.getEntry(STEP_HEIGHT_ATTRIBUTE_ID).get();

	@Override
	public void onEndTick(MinecraftClient client) {
		if (client.player == null) {
			return;
		}

		StepMode stepMode = AccessibleStepOptions.getStepModeOption().getValue();

		EntityAttributeInstance stepHeightAttribute = client.player.getAttributeInstance(STEP_HEIGHT_ATTRIBUTE);

		if (stepMode.equals(StepMode.STEP)) {
			double stepHeight = AccessibleStepOptions.getStepHeightOption().getValue();

			if (client.player.isSneaking()) {
				double sneakHeight = AccessibleStepOptions.getSneakHeightOption().getValue();
				double heightToSet = Math.min(stepHeight, sneakHeight);
				stepHeightAttribute.setBaseValue(heightToSet);
			} else {
				stepHeightAttribute.setBaseValue(stepHeight);
			}
		} else {
			stepHeightAttribute.setBaseValue(AccessibleStepOptions.VANILLA_STEP_HEIGHT);
		}
	}
}
