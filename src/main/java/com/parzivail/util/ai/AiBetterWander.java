package com.parzivail.util.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class AiBetterWander extends EntityAIBase
{
	private final EntityCreature entity;
	private final double speed;

	private double xPosition;
	private double yPosition;
	private double zPosition;

	public AiBetterWander(EntityCreature entity, double speed)
	{
		this.entity = entity;
		this.speed = speed;
		setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if (entity.getRNG().nextInt(5) != 0)
		{
			return false;
		}
		else
		{
			Vec3 vec3 = RandomPositionGenerator.findRandomTarget(entity, 25, 7);

			if (vec3 == null)
			{
				return false;
			}
			else
			{
				xPosition = vec3.xCoord;
				yPosition = vec3.yCoord;
				zPosition = vec3.zCoord;
				return true;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return !entity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		entity.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed);
	}
}
