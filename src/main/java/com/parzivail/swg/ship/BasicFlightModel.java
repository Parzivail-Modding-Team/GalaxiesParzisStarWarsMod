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
		if (this.riddenByEntity == null)
		{
			if (!this.worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		this.setDead();
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		this.prevPosX = this.lastTickPosX = this.posX;
		this.prevPosY = this.lastTickPosY = this.posY;
		this.prevPosZ = this.lastTickPosZ = this.posZ;

		this.rotationYaw = 180 - this.orientation.getYaw();
		this.rotationPitch = this.orientation.getPitch();
		this.prevRotationYaw = 180 - this.previousOrientation.getYaw();
		this.prevRotationPitch = this.previousOrientation.getPitch();

		previousOrientation = orientation.clone();

		if (this.worldObj.isRemote && EntityUtils.isClientControlled(this))
			KeyHandler.handleVehicleMovement();

		if (this.riddenByEntity instanceof EntityLivingBase)
		{
			Vector3f forward = this.orientation.findLocalVectorGlobally(new Vector3f(0, 0, -1));
			//Lumberjack.log(this.throttle);
			this.moveEntity(this.motionX + forward.x * throttle, this.motionY + forward.y * throttle, this.motionZ + forward.z * throttle);
		}
		else
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.orientation.rotateLocalPitch(this.angularMomentum.x);
		this.orientation.rotateLocalYaw(this.angularMomentum.y);
		this.orientation.rotateLocalRoll(this.angularMomentum.z);

		this.angularMomentum.x *= 0.7;
		this.angularMomentum.y *= 0.7;
		this.angularMomentum.z *= 0.7;

		if (Math.abs(this.angularMomentum.x) < 0.001f)
			this.angularMomentum.x = 0;
		if (Math.abs(this.angularMomentum.y) < 0.001f)
			this.angularMomentum.y = 0;
		if (Math.abs(this.angularMomentum.z) < 0.001f)
			this.angularMomentum.z = 0;

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	public void acceptInput(ShipInput input)
	{
		// TODO: seats and delegate input to seats
		switch (input)
		{
			case RollLeft:
				this.angularMomentum.z -= 4;
				break;
			case RollRight:
				this.angularMomentum.z += 4;
				break;
			case PitchUp:
				this.angularMomentum.x += 2;
				break;
			case PitchDown:
				this.angularMomentum.x -= 2;
				break;
			case YawLeft:
				break;
			case YawRight:
				break;
			case ThrottleUp:
				this.throttle += this.data.maxThrottle * this.data.acceleration;
				this.throttle = MathHelper.clamp_float(this.throttle, 0, this.data.maxThrottle);
				break;
			case ThrottleDown:
				this.throttle -= this.data.maxThrottle * this.data.acceleration;
				this.throttle = MathHelper.clamp_float(this.throttle, 0, this.data.maxThrottle);
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
