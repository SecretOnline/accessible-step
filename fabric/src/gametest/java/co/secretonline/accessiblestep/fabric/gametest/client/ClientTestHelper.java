package co.secretonline.accessiblestep.fabric.gametest.client;

import co.secretonline.accessiblestep.State;
import co.secretonline.accessiblestep.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ClientTestHelper {
	public static TestSingleplayerContext createWorld(ClientGameTestContext testContext, String name) {
		return testContext
				.worldBuilder()
				.adjustSettings((worldCreator) -> {
					worldCreator.setName(name);
				})
				.create();
	}

	public static void lookDown(ClientGameTestContext testContext) {
		testContext.runOnClient((client) -> {
			client.player.setXRot(90f);
		});
	}

	public static BlockPos getPlayerPosition(ClientGameTestContext testContext) {
		return testContext.computeOnClient((client) -> {
			return client.player.blockPosition();
		});
	}

	public static void placeTestStructure(TestSingleplayerContext worldContext, BlockPos blockPos) {
		worldContext.getServer().runOnServer((server) -> {
			ServerLevel world = server.getLevel(Level.OVERWORLD);

			world.setBlockAndUpdate(blockPos.south(), Blocks.RED_TERRACOTTA.defaultBlockState());
			world.setBlockAndUpdate(blockPos.south(2).above(1), Blocks.YELLOW_TERRACOTTA.defaultBlockState());
			world.setBlockAndUpdate(blockPos.south(3).above(2), Blocks.GREEN_TERRACOTTA.defaultBlockState());
			world.setBlockAndUpdate(blockPos.south(4).above(3), Blocks.BARRIER.defaultBlockState());
			world.setBlockAndUpdate(blockPos.south(4).above(4), Blocks.BARRIER.defaultBlockState());
		});
	}

	public static void setStepMode(ClientGameTestContext testContext, StepMode mode) {
		testContext.runOnClient((client) -> {
			State.config.setStepMode(mode);
		});
	}
}
