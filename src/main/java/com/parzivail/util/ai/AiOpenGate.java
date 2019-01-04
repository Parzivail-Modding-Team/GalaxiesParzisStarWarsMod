package com.parzivail.util.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class AiOpenGate extends EntityAIBase
{
	protected EntityLiving theEntity;
	protected int entityPosX;
	protected int entityPosY;
	protected int entityPosZ;
	/**
	 * The wooden door block
	 */
	protected BlockFenceGate gateBlock;
	/**
	 * If is true then the Entity has stopped Door Interaction and compoleted the task.
	 */
	boolean hasStoppedDoorInteraction;
	float entityPositionX;
	float entityPositionZ;
	/**
	 * If the entity close the gate
	 */
	boolean closeGate;
	/**
	 * The temporisation before the entity close the gate (in ticks, always 20 = 1 second)
	 */
	int closeGateTemporisation;

	public AiOpenGate(EntityLiving entity, boolean closeGate)
	{
		theEntity = entity;
		this.closeGate = closeGate;
	}

	private void func_150014_a(World worldObj, int x, int y, int z, boolean open)
	{
		int i1 = worldObj.getBlockMetadata(x, y, z);

		if (!open)
		{
			worldObj.setBlockMetadataWithNotify(x, y, z, i1 & -5, 2);
		}
		else
		{
			int j1 = (MathHelper.floor_double((double)(theEntity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
			int k1 = i1 & 3;

			if (k1 == (j1 + 2) % 4)
			{
				i1 = j1;
			}

			worldObj.setBlockMetadataWithNotify(x, y, z, i1 | 4, 2);
		}

		worldObj.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
	}

	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		if (closeGate)
		{
			func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, false);
		}
	}

	public boolean shouldExecute()
	{
		if (!theEntity.isCollidedHorizontally)
		{
			return false;
		}
		else
		{
			PathNavigate pathnavigate = theEntity.getNavigator();
			PathEntity pathentity = pathnavigate.getPath();

			if (pathentity != null && !pathentity.isFinished() && pathnavigate.getCanBreakDoors())
			{
				for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i)
				{
					PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
					entityPosX = pathpoint.xCoord;
					entityPosY = pathpoint.yCoord;
					entityPosZ = pathpoint.zCoord;

					if (theEntity.getDistanceSq((double)entityPosX, theEntity.posY, (double)entityPosZ) <= 2.25D)
					{
						gateBlock = getWoodenDoorBlock(entityPosX, entityPosY, entityPosZ);

						if (gateBlock != null)
						{
							return true;
						}
					}
				}

				entityPosX = MathHelper.floor_double(theEntity.posX);
				entityPosY = MathHelper.floor_double(theEntity.posY);
				entityPosZ = MathHelper.floor_double(theEntity.posZ);
				gateBlock = getWoodenDoorBlock(entityPosX, entityPosY, entityPosZ);
				return gateBlock != null;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return closeGate && closeGateTemporisation > 0 && !hasStoppedDoorInteraction;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		closeGateTemporisation = 20;
		func_150014_a(theEntity.worldObj, entityPosX, entityPosY, entityPosZ, true);
	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		--closeGateTemporisation;

		float f = (float)((double)((float)entityPosX + 0.5F) - theEntity.posX);
		float f1 = (float)((double)((float)entityPosZ + 0.5F) - theEntity.posZ);
		float f2 = entityPositionX * f + entityPositionZ * f1;

		if (f2 < 0.0F)
		{
			hasStoppedDoorInteraction = true;
		}
	}

	/**
	 * Returns the block at the specified position, null if that block is not a wooden door. Args : x, y, z
	 */
	private BlockFenceGate getWoodenDoorBlock(int p_151503_1_, int p_151503_2_, int p_151503_3_)
	{
		Block block = theEntity.worldObj.getBlock(p_151503_1_, p_151503_2_, p_151503_3_);
		return block != Blocks.fence_gate ? null : (BlockFenceGate)block;
	}
}
