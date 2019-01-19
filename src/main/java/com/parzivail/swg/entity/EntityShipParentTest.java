package com.parzivail.swg.entity;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.MessageShipOrientation;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityShipParentTest extends Entity implements IEntityAdditionalSpawnData
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;
	public EntityShipChildTest[] seats;

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
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	public void setDead()
	{
		super.setDead();

		//		if (worldObj.isRemote)
		//			camera.setDead();

		for (EntityShipChildTest seat : seats)
			if (seat != null)
				seat.setDead();
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
		previousOrientation = orientation.clone();

		Entity driver = seats == null ? null : (seats[0] == null ? null : seats[0].riddenByEntity);
		if (driver instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)driver;

			throttle += player.moveForward / 10f;
			throttle = MathHelper.clamp_float(throttle, 0, 1);
			//orientation.rotateLocalYaw(player.moveStrafing * 10);
			orientation.setAngles(-player.rotationYaw, -player.rotationPitch, 0);

			Vector3f forward = orientation.findLocalVectorGlobally(new Vector3f(0, 0, 1));

			if (ticksExisted % 5 == 0 && !worldObj.isRemote)
			{
				EntityTracker tracker = ((WorldServer)worldObj).getEntityTracker();
				IMessage message = new MessageShipOrientation(this);
				for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(this))
					StarWarsGalaxy.network.sendTo(message, (EntityPlayerMP)entityPlayer);
			}

			motionX = forward.x * throttle;
			motionY = forward.y * throttle;
			motionZ = forward.z * throttle;
		}
		else
		{
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}

		if (!worldObj.isRemote && seats != null)
		{
			for (int i = 0; i < seats.length; i++)
			{
				if (seats[i] == null || !seats[i].addedToChunk)
				{
					seats[i] = new EntityShipChildTest(worldObj, this, i);
					worldObj.spawnEntityInWorld(seats[i]);
				}
			}
		}

		moveEntity(motionX, motionY, motionZ);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		setDead();
		return false;
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
		return false;
		//		if (riddenByEntity instanceof EntityPlayer && riddenByEntity != player)
		//			return true;
		//		else
		//		{
		//			if (!worldObj.isRemote)
		//				player.mountEntity(this);
		//			return true;
		//		}
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
		initShip();
	}

	private void initShip()
	{
		seats = new EntityShipChildTest[1];
		Lumberjack.log("Created seat array");
		if (!worldObj.isRemote)
		{
			for (int i = 0; i < seats.length; i++)
			{
				seats[i] = new EntityShipChildTest(worldObj, this, i);
				worldObj.spawnEntityInWorld(seats[i]);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setFloat("throttle", throttle);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
	}

	@Override
	public void readSpawnData(ByteBuf additionalData)
	{
		initShip();

		//spawnCamera();
	}
}
