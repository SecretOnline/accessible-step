package co.secretonline.accessiblestep.util.neoforge;

import java.nio.file.Path;

import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLPaths;

public class ModPaths {
	public static Path getConfigDirectory() {
		return FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
	}
}
