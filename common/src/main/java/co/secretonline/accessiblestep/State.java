package co.secretonline.accessiblestep;

import org.jetbrains.annotations.Nullable;

import co.secretonline.accessiblestep.config.AccessibleStepConfig;
import co.secretonline.accessiblestep.config.AccessibleStepConfigReader;

/** A singleton for all of this mod's state. */
public class State {
	public static AccessibleStepConfigReader configReader = null;
	public static AccessibleStepConfig config = null;
	@Nullable
	public static String worldName = null;
}
