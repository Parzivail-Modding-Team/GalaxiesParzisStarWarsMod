package com.parzivail.util.entity;

import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitBlock;
import com.parzivail.util.math.RaytraceHitEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
	public static RaytraceHit rayTrace(Vec3 fromDir, double distance, Entity fromEntity, Entity[] exclude, boolean includeBlocks)
	{
		if (fromEntity == null || fromEntity.worldObj == null)
			return null;

		Vec3 startPos = Vec3.createVectorHelper(fromEntity.posX, fromEntity.posY, fromEntity.posZ).addVector(0, fromEntity.getEyeHeight(), 0);
		return rayTraceFromPosition(startPos, fromDir, distance, fromEntity, exclude, includeBlocks);
	}

	private static RaytraceHit rayTraceFromPosition(Vec3 startPos, Vec3 fromDir, double distance, Entity fromEntity, Entity[] exclude, boolean includeBlocks)
	{
		Entity pointedEntity = null;
		RaytraceHitBlock rhb = null;
		Vec3 endPos = startPos.addVector(fromDir.xCoord * distance, fromDir.yCoord * distance, fromDir.zCoord * distance);
		List list = fromEntity.worldObj.getEntitiesWithinAABBExcludingEntity(fromEntity, fromEntity.boundingBox.addCoord(fromDir.xCoord * distance, fromDir.yCoord * distance, fromDir.zCoord * distance).expand(1, 1, 1));

		if (includeBlocks)
		{
			Vec3 newEnd = Vec3.createVectorHelper(startPos.xCoord, startPos.yCoord, startPos.zCoord);
			MovingObjectPosition mop = fromEntity.worldObj.rayTraceBlocks(newEnd, endPos);
			if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
			{
				rhb = new RaytraceHitBlock(mop.hitVec, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);
				distance = mop.hitVec.distanceTo(startPos);
				endPos = newEnd;
			}
		}

		for (Object e : list)
		{
			Entity entity = (Entity)e;

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(startPos, endPos);

				if (axisalignedbb.isVecInside(startPos) && (0.0D < distance || distance == 0.0D) && !Arrays.asList(exclude).contains(entity))
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

	//	public static boolean isRiding(Entity entity, Class<? extends Entity> clazz)
	//	{
	//		return !(entity == null || entity.ridingEntity == null) && (clazz.isInstance(entity.ridingEntity) || (entity.ridingEntity instanceof EntitySeat && clazz.isInstance(((EntitySeat)entity.ridingEntity).parent)));
	//	}

	public static Entity getEntityByUuid(World world, UUID uuid)
	{
		for (Object e : world.getLoadedEntityList())
		{
			Entity entity = (Entity)e;
			if (entity.getUniqueID().equals(uuid))
				return entity;
		}
		return null;
	}
}
