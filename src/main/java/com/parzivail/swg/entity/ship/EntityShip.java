package com.parzivail.swg.entity.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityCinematicCamera;
import com.parzivail.swg.network.MessageShipOrientation;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.SlidingWindow;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public abstract class EntityShip extends Entity implements IEntityAdditionalSpawnData
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;
	public EntitySeat[] seats;
	public boolean isInitialized;

	@SideOnly(Side.CLIENT)
	public EntityCinematicCamera camera;
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingPitch = new SlidingWindow(6);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingYaw = new SlidingWindow(5);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingThrottle = new SlidingWindow(8);

	public EntityShip(World worldIn)
	{
		super(worldIn);
		setSize(1, 1);
		orientation = previousOrientation = new RotatedAxes(0, 0, 0);
		angularMomentum = new Vector3f(0, 0, 0);
		throttle = 0;
	}

	@Override
	protected void entityInit()
	{
		if (!worldObj.isRemote)
			createChildren();
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	public abstract ShipData getData();

	@Override
	public void setDead()
	{
		super.setDead();

		if (worldObj.isRemote)
			killCamera();

		for (EntitySeat seat : seats)
			if (seat != null)
				seat.setDead();
	}

	private void killCamera()
	{
		if (camera != null)
			camera.setDead();
	}

	@Override
	public void onUpdate()
	{
		if (ridingEntity != null && ridingEntity.isDead)
			ridingEntity = null;

		ShipData data = getData();

		if (posY < -64.0D)
			kill();

		if (!isInitialized)
		{
			spawnChildren();
			isInitialized = true;
		}

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

			throttle += player.moveForward * data.acceleration;
			throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);

			float p = 0;

			// Ok, player.rotationPitch * 0.999999f is a fun one. So, when pitch = -90 or 90, the math makes a lot of
			// assumptions the matrices should be in (since any yaw with pitch = [-90, 90] is the same location so to
			// combat that we don't let it hit 90, just 89.99991 which is close enough and won't bother anyone.
			if (data.isAirVehicle)
				p = -player.rotationPitch * 0.999999f;
			orientation.setAngles(-player.rotationYaw, p, 0);

			//			if (player.moveForward != 0)
			//				orientation.rotateLocalPitch(player.moveForward);

			Vector3f forward = orientation.findLocalVectorGlobally(new Vector3f(0, 0, 1));

			if (ticksExisted % 5 == 0 && worldObj.isRemote)
				StarWarsGalaxy.network.sendToServer(new MessageShipOrientation(this));

			rotationPitch = orientation.getPitch();
			rotationYaw = orientation.getYaw();

			float dYaw = MathHelper.wrapAngleTo180_float(orientation.getYaw() - previousOrientation.getYaw());
			slidingYaw.slide(dYaw);
			float dPitch = MathHelper.wrapAngleTo180_float(orientation.getPitch() - previousOrientation.getPitch());
			slidingPitch.slide(dPitch);

			slidingThrottle.slide(throttle);

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

		// max distance
		if (data.hasMaxDistance)
		{
			List<AxisAlignedBB> aabb = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMax, 0).addCoord(0, data.distanceMax * 2, 0));
			List<AxisAlignedBB> aabbBig = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMax + 1, 0).addCoord(0, (data.distanceMax + 1) * 2, 0));
			if (aabb.isEmpty())
			{
				if (aabbBig.isEmpty())
					motionY = motionY > 0 ? -data.repulsorliftForce : motionY;
				else
					motionY = motionY > 0 ? 0 : motionY;
			}
		}

		// min distance
		if (data.hasMinDistance)
		{
			List<AxisAlignedBB> aabb = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMin, 0).addCoord(0, data.distanceMin * 2, 0));
			List<AxisAlignedBB> aabbSmall = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMin - 1, 0).addCoord(0, (data.distanceMin - 1) * 2, 0));
			if (!aabb.isEmpty())
			{
				if (aabbSmall.isEmpty())
					motionY = motionY < 0 ? 0 : motionY;
				else
					motionY = motionY < 0 ? data.repulsorliftForce : motionY;
			}
		}

		moveEntity(motionX, motionY, motionZ);
	}

	private void spawnChildren()
	{
		if (worldObj.isRemote)
		{
			spawnCamera();
			return;
		}

		for (int i = 0; i < seats.length; i++)
		{
			// TODO: seat roles
			seats[i] = new EntitySeat(worldObj, this, i);
			worldObj.spawnEntityInWorld(seats[i]);
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
		return false;
	}

	public boolean canBeCollidedWith()
	{
		return false;
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
		orientation = new RotatedAxes(tagCompound.getFloat("yaw"), tagCompound.getFloat("pitch"), tagCompound.getFloat("roll"));
		createChildren();
	}

	private void createChildren()
	{
		seats = new EntitySeat[1];
	}

	private void spawnCamera()
	{
		camera = new EntityCinematicCamera(this);
		worldObj.spawnEntityInWorld(camera);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setFloat("throttle", throttle);
		tagCompound.setFloat("yaw", orientation.getYaw());
		tagCompound.setFloat("pitch", orientation.getPitch());
		tagCompound.setFloat("roll", orientation.getRoll());
	}

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		buffer.writeFloat(orientation.getYaw());
		buffer.writeFloat(orientation.getPitch());
		buffer.writeFloat(orientation.getRoll());
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		orientation = new RotatedAxes(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		createChildren();
	}

	public boolean canBeControlledBy(EntityPlayer thePlayer)
	{
		// TODO:
		return true;
	}
}
