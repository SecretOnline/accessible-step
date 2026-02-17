package co.secretonline.accessiblestep.fabric.gametest.client;

import co.secretonline.accessiblestep.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;

public class StepModeOffTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext testContext) {
		try (TestSingleplayerContext worldContext = ClientTestHelper.createWorld(testContext, "Mode: Off")) {
			ClientTestHelper.lookDown(testContext);
			BlockPos startPosition = ClientTestHelper.getPlayerPosition(testContext);
			ClientTestHelper.placeTestStructure(worldContext, startPosition);
			ClientTestHelper.setStepMode(testContext, StepMode.OFF);

			worldContext.getClientLevel().waitForChunksRender();

			testContext.getInput().holdKeyFor(GLFW.GLFW_KEY_W, 20);

			testContext.takeScreenshot("mode-off");

			testContext.runOnClient((client) -> {
				BlockPos endPosition = client.player.blockPosition();
				Block block = client.player.level().getBlockState(endPosition.below()).getBlock();
				if (!block.equals(Blocks.GRASS_BLOCK)) {
					throw new AssertionError(String.format("Incorrect block. Expected: %s, got %s", Blocks.GRASS_BLOCK, block));
				}
			});
		}
	}
}
