package co.secretonline.accessiblestep;

import org.jetbrains.annotations.Nullable;

import co.secretonline.accessiblestep.options.AccessibleStepConfig;
import co.secretonline.accessiblestep.options.AccessibleStepConfigReader;

public class State {
	public static AccessibleStepConfigReader configReader = null;
	public static AccessibleStepConfig config = null;
	@Nullable
	public static String worldName = null;
}
