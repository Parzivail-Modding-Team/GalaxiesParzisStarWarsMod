package com.parzivail.swg.entity.ship;

import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySeat extends Entity implements IEntityAdditionalSpawnData
{
	@SideOnly(Side.CLIENT)
	public boolean isOrphan = true;
	private EntityShip parent;
	private int parentId;
	private int seatIdx;

	public EntitySeat(World world)
	{
		super(world);
		setSize(1F, 1F);
	}

	/**
	 * Server side seat constructor
	 */
	public EntitySeat(World world, EntityShip parent, int seatIdx)
	{
		this(world);
		this.parent = parent;
		parentId = parent.getEntityId();
		this.seatIdx = seatIdx;
		noClip = true;
		setPosition(parent.posX, parent.posY, parent.posZ);
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (parent != null)
			parent.setDead();
		return false;
	}

	@Override
	public void onUpdate()
	{
		if (worldObj.isRemote && isOrphan)
		{
			parent = (EntityShip)worldObj.getEntityByID(parentId);
			if (parent == null)
				return;
			parent.seats[seatIdx] = this;
			isOrphan = false;
		}
		else if (isOrphan)
			isOrphan = false;

		if (parentId == 0 && ticksExisted > 20)
			setDead();

		trackParent();
	}

	public void trackParent()
	{
		lastTickPosX = prevPosX = posX;
		lastTickPosY = prevPosY = posY;
		lastTickPosZ = prevPosZ = posZ;
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;

		if (parent != null)
		{
			Vector3f seatOffset = parent.getSeatPosition(seatIdx);
			seatOffset = parent.orientation.findLocalVectorGlobally(seatOffset);
			setPosition(parent.posX + seatOffset.x, parent.posY + seatOffset.y, parent.posZ + seatOffset.z);
		}
	}

	public EntityShip getParent()
	{
		return parent;
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
		buffer.writeInt(seatIdx);
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		parentId = buffer.readInt();
		parent = (EntityShip)worldObj.getEntityByID(parentId);
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
