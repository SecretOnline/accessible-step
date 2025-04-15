package co.secretonline.accessiblestep.gametest.client;

import org.junit.jupiter.api.Assertions;
import org.lwjgl.glfw.GLFW;

import co.secretonline.accessiblestep.options.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class StepModeOffTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext testContext) {
		try (TestSingleplayerContext worldContext = ClientTestHelper.createWorld(testContext, "Mode: Off")) {
			ClientTestHelper.lookDown(testContext);
			BlockPos startPosition = ClientTestHelper.getPlayerPosition(testContext);
			ClientTestHelper.placeTestStructure(worldContext, startPosition);
			ClientTestHelper.setStepMode(testContext, StepMode.OFF);

			worldContext.getClientWorld().waitForChunksRender();

			testContext.getInput().holdKeyFor(GLFW.GLFW_KEY_W, 20);

			testContext.takeScreenshot("mode-off");

			testContext.runOnClient((client) -> {
				BlockPos endPosition = client.player.getBlockPos();
				BlockState blockstate = client.player.getWorld().getBlockState(endPosition.down());
				Assertions.assertEquals(Blocks.GRASS_BLOCK, blockstate.getBlock());
			});
		}
	}
}
