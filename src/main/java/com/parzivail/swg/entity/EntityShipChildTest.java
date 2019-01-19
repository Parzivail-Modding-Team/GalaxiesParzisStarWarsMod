package com.parzivail.swg.entity;

import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityShipChildTest extends Entity implements IEntityAdditionalSpawnData
{
	@SideOnly(Side.CLIENT)
	public boolean isOrphan = true;
	private EntityShipParentTest parent;
	private int parentId;
	private int seatIdx;

	public EntityShipChildTest(World world)
	{
		super(world);
		setSize(1F, 1F);
	}

	/**
	 * Server side seat constructor
	 */
	public EntityShipChildTest(World world, EntityShipParentTest parent, int seatIdx)
	{
		this(world);
		this.parent = parent;
		parentId = parent.getEntityId();
		this.seatIdx = seatIdx;
		setPosition(parent.posX, parent.posY, parent.posZ);
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	public void onUpdate()
	{
		if (worldObj.isRemote && isOrphan)
		{
			parent = (EntityShipParentTest)worldObj.getEntityByID(parentId);
			if (parent == null)
				return;
			parent.seats[seatIdx] = this;
			isOrphan = false;
		}

		if (parentId == 0 && ticksExisted > 20)
			setDead();

		if (parent != null)
			setPosition(parent.posX, parent.posY, parent.posZ);
	}

	public boolean canBeCollidedWith()
	{
		return !isDead;
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

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund)
	{

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		buffer.writeInt(parentId);
		Lumberjack.log("Wrote parent ID %s", parentId);
		buffer.writeInt(seatIdx);
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		parentId = buffer.readInt();
		Lumberjack.log("Read parent ID %s", parentId);
		parent = (EntityShipParentTest)worldObj.getEntityByID(parentId);
		seatIdx = buffer.readInt();
		if (parent != null)
		{
			//seatInfo = parent.getDriveableType().seats[seatID];
			parent.seats[seatIdx] = this;
			posX = parent.posX;
			posY = parent.posY;
			posZ = parent.posZ;
			setPosition(posX, posY, posZ);
		}
	}
}
