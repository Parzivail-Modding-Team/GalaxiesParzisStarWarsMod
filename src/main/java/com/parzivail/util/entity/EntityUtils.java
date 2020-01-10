package com.parzivail.util.entity;

import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitBlock;
import com.parzivail.util.math.RaytraceHitEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class EntityUtils
{
	/**
	 * Ray-traces from the given entity's POV
	 *
	 * @param distance   The range of the trace
	 * @param fromEntity The POV entity
	 * @param exclude    The entity references to exclude (Note: not classes, but
	 *                   inequality between two entity pointers)
	 *
	 * @return Returns the entity the trace hit, or null if none is hit
	 */
	public static RaytraceHit rayTrace(Vec3d fromDir, double distance, Entity fromEntity, Entity[] exclude, boolean includeBlocks)
	{
		if (fromEntity == null || fromEntity.world == null)
			return null;

		Vec3d startPos = new Vec3d(fromEntity.posX, fromEntity.posY, fromEntity.posZ).addVector(0, fromEntity.getEyeHeight(), 0);
		return rayTraceFromPosition(startPos, fromDir, distance, fromEntity, exclude, includeBlocks);
	}

	private static RaytraceHit rayTraceFromPosition(Vec3d startPos, Vec3d fromDir, double distance, Entity fromEntity, Entity[] exclude, boolean includeBlocks)
	{
		Entity pointedEntity = null;
		RaytraceHitBlock rhb = null;
		Vec3d endPos = startPos.addVector(fromDir.x * distance, fromDir.y * distance, fromDir.z * distance);
		List list = fromEntity.world.getEntitiesWithinAABBExcludingEntity(fromEntity, fromEntity.getEntityBoundingBox().offset(fromDir.x * distance, fromDir.y * distance, fromDir.z * distance).expand(1, 1, 1));

		if (includeBlocks)
		{
			Vec3d newEnd = new Vec3d(startPos.x, startPos.y, startPos.z);
			RayTraceResult mop = fromEntity.world.rayTraceBlocks(newEnd, endPos);
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				rhb = new RaytraceHitBlock(mop.hitVec, mop.getBlockPos(), mop.sideHit);
				distance = mop.hitVec.distanceTo(startPos);
				endPos = newEnd;
			}
		}

		List<Entity> lExclude = Arrays.asList(exclude);
		for (Object e : list)
		{
			Entity entity = (Entity)e;

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f2, f2, f2);
				RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(startPos, endPos);

				if (axisalignedbb.contains(startPos) && (0.0D < distance || distance == 0.0D) && !lExclude.contains(entity))
				{
					pointedEntity = entity;
					distance = 0.0D;
				}
				else if (movingobjectposition != null)
				{
					double d3 = startPos.distanceTo(movingobjectposition.hitVec);

					if ((d3 < distance || distance == 0.0D) && !Arrays.asList(exclude).contains(entity))
					{
						pointedEntity = entity;
						distance = d3;
					}
				}
			}
		}

		if (pointedEntity != null)
			return new RaytraceHitEntity(pointedEntity);

		return rhb;
	}

	public static RaytraceHit rayTrace(Entity from, double distance)
	{
		return rayTrace(from.getLookVec(), distance, from, new Entity[0], false);
	}
}
