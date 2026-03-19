package dev.pswg.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class WorldUtil
{
	public static boolean isSunLit(ServerLevel world, BlockPos pos)
	{
		return getSunlight(world, pos) > 0;
	}

	public static boolean isNightTime(ServerLevel world)
	{
		return (world.getGameTime() - 6000) % 24000 > 12000;
	}

	private static int getSunlight(ServerLevel world, BlockPos pos)
	{
		if (!world.dimensionType().hasSkyLight())
			return 0;

		var skyLight = world.getBrightness(LightLayer.SKY, pos) - world.getSkyDarken();
		var skyAngle = world.environmentAttributes().getDimensionValue(EnvironmentAttributes.SUN_ANGLE);

		float upperBoundAngle = skyAngle < Math.PI ? 0.0F : (float)(2 * Math.PI);
		skyAngle += (upperBoundAngle - skyAngle) * 0.2F;
		skyLight = Math.round(skyLight * Mth.cos(skyAngle));

		return Mth.clamp(skyLight, 0, 15);
	}

	public static void destroyDoubleBlockFromBottom(Level world, BlockPos pos, BlockState state, Player player)
	{
		DoubleBlockHalf doubleBlockHalf = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER)
		{
			BlockPos blockPos = pos.below();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.is(state.getBlock()) && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			{
				BlockState blockState2 = blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				world.setBlock(blockPos, blockState2, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
				world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
			}
		}
	}
}
