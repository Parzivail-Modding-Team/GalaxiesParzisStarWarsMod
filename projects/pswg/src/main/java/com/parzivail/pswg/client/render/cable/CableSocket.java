package com.parzivail.pswg.client.render.cable;

import com.parzivail.pswg.block.PowerCouplingBlock;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.Pose3f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

/*
	Based on: https://github.com/FoundationGames/Phonos
 */
public class CableSocket
{
	private BlockPos blockPos;
	private Pose3f pose;

	public CableSocket(BlockPos blockPos, Pose3f pose)
	{
		this.blockPos = blockPos;
		this.pose = pose;
	}

	public void writePlugPose(World world, float delta, Pose3f out)
	{
		out.set(this.pose);
	}

	public void writeOriginPose(World world, float delta, Pose3f out)
	{
		out.pos().set(this.blockPos.getX() + 0.5f, this.blockPos.getY() + 0.5f, this.blockPos.getZ() + 0.5f);

		if (world.isPosLoaded(this.blockPos.getX(), this.blockPos.getZ()))
		{
			if (world.getBlockState(this.blockPos).getBlock() instanceof PowerCouplingBlock)
			{
				out.rotation().set(MathUtil.getEastRotation(world.getBlockState(blockPos).get(PowerCouplingBlock.FACING)));
				return;
			}
		}

		out.rotation().set(RotationAxis.POSITIVE_Y.rotation(0));
	}
}
