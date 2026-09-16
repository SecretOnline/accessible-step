package co.secretonline.accessiblestep.fabric.gametest.client;

import co.secretonline.accessiblestep.StepMode;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class StepModeAutoJumpTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext testContext) {
		try (TestSingleplayerContext worldContext = ClientTestHelper.createWorld(testContext, "Mode: Auto-jump")) {
			ClientTestHelper.lookDown(testContext);
			BlockPos startPosition = ClientTestHelper.getPlayerPosition(testContext);
			ClientTestHelper.placeTestStructure(worldContext, startPosition);
			ClientTestHelper.setStepMode(testContext, StepMode.AUTO_JUMP);

			worldContext.getConnection().waitForChunksRender();

			testContext.getInput().holdKeyFor(InputConstants.KEY_W, 20);

			testContext.takeScreenshot("mode-autojump");

			testContext.runOnClient((client) -> {
				BlockPos endPosition = client.player.blockPosition();
				Block block = client.player.level().getBlockState(endPosition.below()).getBlock();
				if (!block.equals(Blocks.DYED_TERRACOTTA.yellow())) {
					throw new AssertionError(String.format("Incorrect block. Expected: %s, got %s", Blocks.DYED_TERRACOTTA.yellow(), block));
				}
			});
		}
	}
}
