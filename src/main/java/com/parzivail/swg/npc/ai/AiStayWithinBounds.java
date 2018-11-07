package com.parzivail.swg.npc.ai;

import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.world.Zone;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class AiStayWithinBounds extends EntityAIBase
{
	private final EntityCreature theEntity;
	private final Zone zone;
	private final double movementSpeed;

	private double movePosX;
	private double movePosY;
	private double movePosZ;

	public AiStayWithinBounds(EntityCreature entity, Zone zone, double speed)
	{
		theEntity = entity;
		this.zone = zone;
		movementSpeed = speed;
		setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if (zone.dimension != theEntity.dimension || zone.contains(theEntity))
			return false;
		else
		{
			Vector3f chunkcoordinates = zone.getCenter();
			Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(theEntity, 16, 7, Vec3.createVectorHelper((double)chunkcoordinates.x, (double)chunkcoordinates.y, (double)chunkcoordinates.z));

			if (vec3 == null)
			{
				return false;
			}
			else
			{
				movePosX = vec3.xCoord;
				movePosY = vec3.yCoord;
				movePosZ = vec3.zCoord;
				return true;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return !theEntity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		theEntity.getNavigator().tryMoveToXYZ(movePosX, movePosY, movePosZ, movementSpeed);
	}
}
