package co.secretonline.accessiblestep.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessor {
	/**
	 * The ClientPlayNetworkHandler is used on erver join to get the ServerInfo for
	 * the current world.
	 * Fabric's network join event passes the ClientPlayNetworkHandler but
	 * (Neo)Forge doesn't, so we need to get it somehow.
	 */
	@Accessor
	public ClientPlayNetworkHandler getNetworkHandler();
}
