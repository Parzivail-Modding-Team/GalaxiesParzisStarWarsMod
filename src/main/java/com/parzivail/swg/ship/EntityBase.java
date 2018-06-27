package com.parzivail.swg.ship;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by colby on 12/27/2017.
 */
public abstract class EntityBase extends EntityLivingBase
{
	EntityBase(World world)
	{
		super(world);
	}

	/**
	 * Tries to moves the entity by the passed in displacement. Args: x, y, z
	 */
	public void moveEntity(double dx, double dy, double dz)
	{
		double tempDX = dx;
		double tempDY = dy;
		double tempDZ = dz;

		List nearAABBs = worldObj.getCollidingBoundingBoxes(this, boundingBox.addCoord(dx, dy, dz));

		for (Object nearAABB : nearAABBs)
		{
			dy = ((AxisAlignedBB)nearAABB).calculateYOffset(boundingBox, dy);
			dx = ((AxisAlignedBB)nearAABB).calculateXOffset(boundingBox, dx);
			dz = ((AxisAlignedBB)nearAABB).calculateZOffset(boundingBox, dz);
		}
		boundingBox.offset(dx, dy, dz);

		posX += dx;
		posY += dy;
		posZ += dz;
		isCollidedHorizontally = tempDX != dx || tempDZ != dz;
		isCollidedVertically = tempDY != dy;
		onGround = tempDY != dy && tempDY < 0.0D;
		isCollided = isCollidedHorizontally || isCollidedVertically;

		if (tempDX != dx)
			motionX = 0.0D;

		if (tempDY != dy)
			motionY = 0.0D;

		if (tempDZ != dz)
			motionZ = 0.0D;

		try
		{
			doBlockCollisions();
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{

	}

	@Override
	public ItemStack getHeldItem()
	{
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int p_71124_1_)
	{
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
	{

	}
}
