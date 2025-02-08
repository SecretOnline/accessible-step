package co.secretonline.accessiblestep.neoforge;

import net.neoforged.fml.common.Mod;

import co.secretonline.accessiblestep.AccessibleStep;

@Mod(AccessibleStep.MOD_ID)
public final class ExampleModNeoForge {
	public ExampleModNeoForge() {
		// Run our common setup.
		AccessibleStep.init();
	}
}
