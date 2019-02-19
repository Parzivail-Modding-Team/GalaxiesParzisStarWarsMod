package com.parzivail.swg.ship;

import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityLegacySeat extends Entity
{
	/**
	 * The dragon entity this dragon part belongs to
	 */
	public EntityLegacyShip parent;
	public String name;
	public SeatRole role;
	public Vector3f position;

	public EntityLegacySeat(World world)
	{
		super(world);
		setSize(0.5f, 0.5f);
		noClip = true;
	}

	public EntityLegacySeat(EntityLegacyShip parent, String name, SeatRole role, Vector3f position)
	{
		this(parent.worldObj);
		this.parent = parent;
		this.name = name;
		this.role = role;
		this.position = position;
	}

	protected void entityInit()
	{
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
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
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		name = tagCompound.getString("name");
		role = SeatRole.valueOf(tagCompound.getString("role"));
		position = new Vector3f(tagCompound.getFloat("pX"), tagCompound.getFloat("pY"), tagCompound.getFloat("pZ"));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setString("name", name);
		tagCompound.setString("role", role.toString());
		tagCompound.setFloat("pX", position.x);
		tagCompound.setFloat("pY", position.y);
		tagCompound.setFloat("pZ", position.z);
	}

	/**
	 * Returns true if Entity argument is equal to this Entity
	 */
	public boolean isEntityEqual(Entity entityIn)
	{
		return this == entityIn || parent == entityIn;
	}

	public void setLocation()
	{
		if (parent != null)
			setLocationAndAngles(parent.posX + position.x, parent.posY + position.y, parent.posZ + position.z, 0, 0);
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
		return false;
	}

	public void setParent(int parentId)
	{
		Entity entity = worldObj.getEntityByID(parentId);

		if (entity == null)
		{
			Lumberjack.warn("Part failed to locate parent");
			//setDead();
		}
		else
		{
			if (!(entity instanceof EntityLegacyShip))
				return;

			parent = (EntityLegacyShip)entity;
			Lumberjack.warn("Part located parent");
		}
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
			player.mountEntity(this);

			return true;
		}
	}
}
