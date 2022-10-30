package com.parzivail.util.world;

import com.parzivail.pswg.block.Sliding1x2DoorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class WorldUtil
{
	public static boolean isSunLit(ServerWorld world, BlockPos pos)
	{
		return getSunlight(world, pos) > 0;
	}

	public static boolean isNightTime(ServerWorld world)
	{
		return (world.getTime() - 6000) % 24000 > 12000;
	}

	private static int getSunlight(ServerWorld world, BlockPos pos)
	{
		if (!world.getDimension().hasSkyLight())
			return 0;

		var skyLight = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
		var skyAngle = world.getSkyAngleRadians(1.0F);

		var upperBoundAngle = skyAngle < Math.PI ? 0 : (2 * Math.PI);
		skyAngle += (upperBoundAngle - skyAngle) * 0.2F;
		skyLight = Math.round(skyLight * MathHelper.cos(skyAngle));

		return MathHelper.clamp(skyLight, 0, 15);
	}

	public static void destroyDoubleBlockFromBottom(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		DoubleBlockHalf doubleBlockHalf = state.get(Sliding1x2DoorBlock.HALF);
		if (doubleBlockHalf == DoubleBlockHalf.UPPER)
		{
			BlockPos blockPos = pos.down();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(state.getBlock()) && blockState.get(Sliding1x2DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			{
				BlockState blockState2 = blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
				world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL | Block.SKIP_DROPS);
				world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
			}
		}
	}
}
