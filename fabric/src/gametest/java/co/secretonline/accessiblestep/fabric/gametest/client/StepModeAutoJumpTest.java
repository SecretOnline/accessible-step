package co.secretonline.accessiblestep.fabric.gametest.client;

import org.junit.jupiter.api.Assertions;
import org.lwjgl.glfw.GLFW;

import co.secretonline.accessiblestep.StepMode;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class StepModeAutoJumpTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext testContext) {
		try (TestSingleplayerContext worldContext = ClientTestHelper.createWorld(testContext, "Mode: Auto-jump")) {
			ClientTestHelper.lookDown(testContext);
			BlockPos startPosition = ClientTestHelper.getPlayerPosition(testContext);
			ClientTestHelper.placeTestStructure(worldContext, startPosition);
			ClientTestHelper.setStepMode(testContext, StepMode.AUTO_JUMP);

			worldContext.getClientWorld().waitForChunksRender();

			testContext.getInput().holdKeyFor(GLFW.GLFW_KEY_W, 20);

			testContext.takeScreenshot("mode-autojump");

			testContext.runOnClient((client) -> {
				BlockPos endPosition = client.player.getBlockPos();
				BlockState blockstate = client.player.getEntityWorld().getBlockState(endPosition.down());
				Assertions.assertEquals(Blocks.YELLOW_TERRACOTTA, blockstate.getBlock());
			});
		}
	}
}
