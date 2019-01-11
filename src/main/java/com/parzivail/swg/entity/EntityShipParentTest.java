package com.parzivail.swg.entity;

import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityShipParentTest extends Entity
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;

	public EntityShipParentTest(World worldIn)
	{
		super(worldIn);
		setSize(1, 1);
		orientation = previousOrientation = new RotatedAxes(0, 0, 0);
		angularMomentum = new Vector3f(0, 0, 0);
		throttle = 0;
	}

	public EntityShipParentTest(World worldIn, int x, int y, int z)
	{
		this(worldIn);
		setPosition(x + 0.5, y + 0.5f, z + 0.5);
	}

	@Override
	protected void entityInit()
	{
	}

	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		velocityX = motionX = x;
		velocityY = motionY = y;
		velocityZ = motionZ = z;
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		if (!worldObj.isRemote)
			setPosition(x, y, z);
		setRotation(yaw, pitch);
		//		motionX = velocityX;
		//		motionY = velocityY;
		//		motionZ = velocityZ;
	}

	@Override
	public void onUpdate()
	{
		if (ridingEntity != null && ridingEntity.isDead)
			ridingEntity = null;

		if (posY < -64.0D)
			kill();

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;
		previousOrientation = orientation;

		if (riddenByEntity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)riddenByEntity;
			float a = (float)((player.rotationYaw + 90) / 180 * Math.PI);
			motionX = player.moveForward * MathHelper.cos(a);
			motionY = 0;
			motionZ = player.moveForward * MathHelper.sin(a);
		}
		else
		{
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}

		moveEntity(motionX, motionY, motionZ);
	}

	public void updateRiderPosition()
	{
		if (riddenByEntity == null)
			return;

		riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
	}

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if (riddenByEntity instanceof EntityPlayer && riddenByEntity != player)
			return true;
		else
		{
			if (!worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
	}

	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return 0.6;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		throttle = tagCompound.getFloat("throttle");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setFloat("throttle", throttle);
	}
}
