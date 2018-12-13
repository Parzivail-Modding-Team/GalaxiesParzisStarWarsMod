package com.parzivail.swg.entity;

import com.parzivail.swg.block.BlockChair;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityChair extends Entity
{
	private int px;
	private int py;
	private int pz;

	public EntityChair(World worldIn)
	{
		super(worldIn);
		setSize(0.8f, 0.8f);
		noClip = true;
	}

	public EntityChair(World worldIn, int x, int y, int z)
	{
		this(worldIn);
		px = x;
		py = y;
		pz = z;
		setPosition(x + 0.5, y + 0.1f, z + 0.5);
	}

	@Override
	protected void entityInit()
	{
	}

	@Override
	public void onUpdate()
	{
		if (ridingEntity != null && ridingEntity.isDead)
			ridingEntity = null;

		if (posY < -64.0D)
			kill();

		if (!worldObj.isRemote && riddenByEntity == null && !(worldObj.getBlock(px, py, pz) instanceof BlockChair))
			kill();

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	protected boolean shouldSetPosAfterLoading()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return 0;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		px = tagCompound.getInteger("px");
		py = tagCompound.getInteger("py");
		pz = tagCompound.getInteger("pz");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setInteger("px", px);
		tagCompound.setInteger("py", py);
		tagCompound.setInteger("pz", pz);
	}
}
