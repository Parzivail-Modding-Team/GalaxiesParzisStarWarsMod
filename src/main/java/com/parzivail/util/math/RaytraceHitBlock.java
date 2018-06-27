package com.parzivail.util.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class RaytraceHitBlock extends RaytraceHit
{
	private static final EnumFacing[] faceDecode = {
			EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST
	};

	public final Vec3 hitVec;
	public final int blockX;
	public final int blockY;
	public final int blockZ;
	public final int sideHit;
	public final EnumFacing sideHitFace;

	public RaytraceHitBlock(Vec3 hitVec, int blockX, int blockY, int blockZ, int sideHit)
	{
		this.hitVec = hitVec;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.sideHit = sideHit;
		sideHitFace = faceDecode[sideHit];
	}
}
