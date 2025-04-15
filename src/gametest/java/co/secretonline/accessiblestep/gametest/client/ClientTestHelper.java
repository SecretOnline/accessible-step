package co.secretonline.accessiblestep.gametest.client;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientTestHelper {
	public static TestSingleplayerContext createWorld(ClientGameTestContext testContext, String name) {
		return testContext
				.worldBuilder()
				.adjustSettings((worldCreator) -> {
					worldCreator.setWorldName(name);
				})
				.create();
	}

	public static void lookDown(ClientGameTestContext testContext) {
		testContext.runOnClient((client) -> {
			client.player.setPitch(90f);
		});
	}

	public static BlockPos getPlayerPosition(ClientGameTestContext testContext) {
		return testContext.computeOnClient((client) -> {
			return client.player.getBlockPos();
		});
	}

	public static void placeTestStructure(TestSingleplayerContext worldContext, BlockPos blockPos) {
		worldContext.getServer().runOnServer((server) -> {
			ServerWorld world = server.getWorld(World.OVERWORLD);

			world.setBlockState(blockPos.south(), Blocks.RED_TERRACOTTA.getDefaultState());
			world.setBlockState(blockPos.south(2).up(1), Blocks.YELLOW_TERRACOTTA.getDefaultState());
			world.setBlockState(blockPos.south(3).up(2), Blocks.GREEN_TERRACOTTA.getDefaultState());
			world.setBlockState(blockPos.south(4).up(3), Blocks.BARRIER.getDefaultState());
			world.setBlockState(blockPos.south(4).up(4), Blocks.BARRIER.getDefaultState());
		});
	}

	public static void setStepMode(ClientGameTestContext testContext, StepMode mode) {
		testContext.runOnClient((client) -> {
			State.config.setStepMode(mode);
		});
	}
}
