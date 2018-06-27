package com.parzivail.swg.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.swg.network.MessageFlightModelUpdate;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/26/2017.
 */
public abstract class BasicFlightModel extends EntityBase
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;

	/**
	 * Distance from the bottom of the model to the "center" of it, from which it will rotate in a roll
	 */
	public float verticalCenteringOffset;
	/**
	 * Distance we need to translate the model up to make sure the bottom is in line with the entity bottom
	 */
	public float verticalGroundingOffset;

	public ShipData data;
	protected Vector3f[] seatOffsets;

	public BasicFlightModel(World world)
	{
		super(world);

		setSize(0.5F, 0.5F);

		preventEntitySpawning = true;
		ignoreFrustumCheck = true;
		renderDistanceWeight = 200D;

		orientation = previousOrientation = new RotatedAxes();
		angularMomentum = new Vector3f();

		//		createSeats();
		//
		//		for (int i = 0; i < this.seats.length; i++)
		//			this.dataWatcher.addObject(DATA_SEATID + i, 0);

		createData();
	}

	protected void setPivots(float verticalCenteringOffset, float verticalGroundingOffset)
	{
		this.verticalCenteringOffset = verticalCenteringOffset;
		this.verticalGroundingOffset = verticalGroundingOffset;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	protected void createData()
	{
		data = new ShipData();
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public void applyEntityCollision(Entity other)
	{
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer player)
	{
		if (riddenByEntity == null)
		{
			if (!worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		setDead();
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (posY < -64.0D)
			setDead();

		prevPosX = lastTickPosX = posX;
		prevPosY = lastTickPosY = posY;
		prevPosZ = lastTickPosZ = posZ;

		rotationYaw = 180 - orientation.getYaw();
		rotationPitch = orientation.getPitch();
		prevRotationYaw = 180 - previousOrientation.getYaw();
		prevRotationPitch = previousOrientation.getPitch();

		previousOrientation = orientation.clone();

		if (worldObj.isRemote && EntityUtils.isClientControlled(this))
			KeyHandler.handleVehicleMovement();

		if (riddenByEntity instanceof EntityLivingBase)
		{
			Vector3f forward = orientation.findLocalVectorGlobally(new Vector3f(0, 0, -1));
			//Lumberjack.log(this.throttle);
			moveEntity(motionX + forward.x * throttle, motionY + forward.y * throttle, motionZ + forward.z * throttle);
		}
		else
			moveEntity(motionX, motionY, motionZ);
		orientation.rotateLocalPitch(angularMomentum.x);
		orientation.rotateLocalYaw(angularMomentum.y);
		orientation.rotateLocalRoll(angularMomentum.z);

		angularMomentum.x *= 0.7;
		angularMomentum.y *= 0.7;
		angularMomentum.z *= 0.7;

		if (Math.abs(angularMomentum.x) < 0.001f)
			angularMomentum.x = 0;
		if (Math.abs(angularMomentum.y) < 0.001f)
			angularMomentum.y = 0;
		if (Math.abs(angularMomentum.z) < 0.001f)
			angularMomentum.z = 0;

		if (!worldObj.isRemote)
		{
			if (riddenByEntity != null && riddenByEntity.isDead)
				riddenByEntity = null;
		}
	}

	public void acceptInput(ShipInput input)
	{
		// TODO: seats and delegate input to seats
		switch (input)
		{
			case RollLeft:
				angularMomentum.z -= 4;
				break;
			case RollRight:
				angularMomentum.z += 4;
				break;
			case PitchUp:
				angularMomentum.x += 2;
				break;
			case PitchDown:
				angularMomentum.x -= 2;
				break;
			case YawLeft:
				break;
			case YawRight:
				break;
			case ThrottleUp:
				throttle += data.maxThrottle * data.acceleration;
				throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);
				break;
			case ThrottleDown:
				throttle -= data.maxThrottle * data.acceleration;
				throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);
				break;
			case BlasterFire:
				break;
			case SpecialAesthetic:
				break;
			case SpecialWeapon:
				break;
		}
		StarWarsGalaxy.network.sendToServer(new MessageFlightModelUpdate(this));
	}
}
