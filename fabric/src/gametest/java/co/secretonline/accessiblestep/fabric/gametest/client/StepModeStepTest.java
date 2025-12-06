package co.secretonline.accessiblestep.fabric.gametest.client;

import org.junit.jupiter.api.Assertions;
import org.lwjgl.glfw.GLFW;

import co.secretonline.accessiblestep.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

public class StepModeStepTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext testContext) {
		try (TestSingleplayerContext worldContext = ClientTestHelper.createWorld(testContext, "Mode: Step")) {
			ClientTestHelper.lookDown(testContext);
			BlockPos startPosition = ClientTestHelper.getPlayerPosition(testContext);
			ClientTestHelper.placeTestStructure(worldContext, startPosition);
			ClientTestHelper.setStepMode(testContext, StepMode.STEP);

			worldContext.getClientWorld().waitForChunksRender();

			testContext.getInput().holdKeyFor(GLFW.GLFW_KEY_W, 20);

			testContext.takeScreenshot("mode-step");

			testContext.runOnClient((client) -> {
				BlockPos endPosition = client.player.blockPosition();
				BlockState blockstate = client.player.level().getBlockState(endPosition.below());
				Assertions.assertEquals(Blocks.GREEN_TERRACOTTA, blockstate.getBlock());
			});
		}
	}
}
