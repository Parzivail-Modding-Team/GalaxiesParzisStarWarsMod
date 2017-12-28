package com.parzivail.swg.ship;

import com.parzivail.util.item.ItemUtils;
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

		List nearAABBs = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(dx, dy, dz));

		for (Object nearAABB : nearAABBs)
		{
			dy = ((AxisAlignedBB)nearAABB).calculateYOffset(this.boundingBox, dy);
			dx = ((AxisAlignedBB)nearAABB).calculateXOffset(this.boundingBox, dx);
			dz = ((AxisAlignedBB)nearAABB).calculateZOffset(this.boundingBox, dz);
		}
		this.boundingBox.offset(dx, dy, dz);

		this.posX += dx;
		this.posY += dy;
		this.posZ += dz;
		this.isCollidedHorizontally = tempDX != dx || tempDZ != dz;
		this.isCollidedVertically = tempDY != dy;
		this.onGround = tempDY != dy && tempDY < 0.0D;
		this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;

		if (tempDX != dx)
			this.motionX = 0.0D;

		if (tempDY != dy)
			this.motionY = 0.0D;

		if (tempDZ != dz)
			this.motionZ = 0.0D;

		try
		{
			this.func_145775_I();
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			this.addEntityCrashInfo(crashreportcategory);
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
		return ItemUtils.ITEMSTACK_EMPTY;
	}

	@Override
	public ItemStack getEquipmentInSlot(int p_71124_1_)
	{
		return ItemUtils.ITEMSTACK_EMPTY;
	}

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
	{

	}

	@Override
	public ItemStack[] getLastActiveItems()
	{
		return ItemUtils.ITEMSTACK_EMPTY_ARRAY;
	}
}
