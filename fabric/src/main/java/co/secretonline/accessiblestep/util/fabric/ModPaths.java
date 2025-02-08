package co.secretonline.accessiblestep.util.fabric;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class ModPaths {
	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
