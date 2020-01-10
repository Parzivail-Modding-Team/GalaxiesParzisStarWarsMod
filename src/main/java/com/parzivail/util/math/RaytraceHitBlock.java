package com.parzivail.util.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RaytraceHitBlock extends RaytraceHit
{
	public final Vec3d hitVec;
	public final BlockPos pos;
	public final EnumFacing sideHitFace;

	public RaytraceHitBlock(Vec3d hitVec, BlockPos pos, EnumFacing sideHit)
	{
		this.hitVec = hitVec;
		this.pos = pos;
		this.sideHitFace = sideHit;
	}
}
