package co.secretonline.accessiblestep.util;

import java.nio.file.Path;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class ModPaths {
	@ExpectPlatform
	public static Path getConfigDirectory() {
		// This gets replaced at runtime.
		// Architectury API has a method to do this, but I'm trying to avoid adding new
		// dependencies to the project.
		throw new AssertionError();
	}
}
